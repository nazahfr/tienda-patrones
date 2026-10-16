import subprocess, html

def node(name, kind=None, attrs=(), methods=(), fill="#FFFFFF"):
    esc = html.escape
    head = esc(name)
    if kind == "interface": head = f"&laquo;interface&raquo;<BR/><B>{esc(name)}</B>"
    elif kind == "abstract": head = f"<I><B>{esc(name)}</B></I>"
    elif kind == "enum": head = f"&laquo;enum&raquo;<BR/><B>{esc(name)}</B>"
    else: head = f"<B>{esc(name)}</B>"
    def rows(items):
        if not items: return '<TR><TD ALIGN="LEFT"> </TD></TR>'
        return "".join(f'<TR><TD ALIGN="LEFT">{esc(i)}</TD></TR>' for i in items)
    return (f'{name} [label=<<TABLE BORDER="1" CELLBORDER="0" CELLSPACING="0" CELLPADDING="3" BGCOLOR="{fill}">'
            f'<TR><TD>{head}</TD></TR><HR/>{rows(attrs)}<HR/>{rows(methods)}</TABLE>>];')

EDGE = {
 "inherit":  'arrowhead=empty, style=solid',
 "realize":  'arrowhead=empty, style=dashed',
 "depend":   'arrowhead=vee, style=dashed',
 "assoc":    'arrowhead=vee, style=solid',
 "agg":      'dir=back, arrowtail=odiamond, arrowhead=none, style=solid',
 "comp":     'dir=back, arrowtail=diamond, arrowhead=none, style=solid',
}
def edge(a, b, kind, label=""):
    lab = f', label="{label}", fontsize=9' if label else ""
    return f'{a} -> {b} [{EDGE[kind]}{lab}];'

def render(fname, title, nodes, edges, rankdir="BT"):
    dot = ('digraph G {\n rankdir=%s; nodesep=0.5; ranksep=0.7; labelloc=t; fontsize=14; label="%s";\n'
           ' node [shape=plain, fontname="Helvetica", fontsize=10];\n edge [fontname="Helvetica", fontsize=9];\n') % (rankdir, title)
    dot += "\n".join(nodes) + "\n" + "\n".join(edges) + "\n}\n"
    open(fname + ".dot", "w").write(dot)
    subprocess.run(["dot", "-Tpng", "-Gdpi=170", fname + ".dot", "-o", fname + ".png"], check=True)

PROD = "#E8F0FE"; PAT = "#FFF4CE"

# ---------- 1 Singleton ----------
render("uml_singleton", "Singleton: ConfiguracionApp y ConexionBD", [
 node("ConfiguracionApp", attrs=["-nombreTienda: String","-moneda: String","-igv: double"],
      methods=["-ConfiguracionApp()","+getInstancia(): ConfiguracionApp {static}","+getIgv(): double","+getMoneda(): String"], fill=PAT),
 node("ConexionBD", attrs=["-instancia: ConexionBD {static, volatile}","-url: String","-conexion: Connection"],
      methods=["-ConexionBD()","+getInstancia(): ConexionBD {static}","+getConexion(): Connection","+getUrl(): String"], fill=PAT),
 node("Holder", attrs=["-INSTANCIA: ConfiguracionApp {static final}"], methods=[]),
 node("ConIgv", methods=["+calcular(): double"]),
], [
 edge("ConfiguracionApp","Holder","comp","contiene"),
 edge("ConIgv","ConfiguracionApp","depend","usa getIgv()"),
], rankdir="TB")

# ---------- 2 Prototype ----------
render("uml_prototype", "Prototype: Producto, CatalogoPrototipos y Pedido", [
 node("Producto","abstract",["-sku: String","-nombre: String","-talla: String","-precio: double","-stock: int"],
      ["+clone(): Producto","+clonarVariante(sku, talla, stock): Producto","+tipo(): String {abstract}"], fill=PAT),
 node("Polera", methods=["+tipo(): String"], fill=PROD),
 node("Pantalon", methods=["+tipo(): String"], fill=PROD),
 node("Zapatilla", methods=["+tipo(): String"], fill=PROD),
 node("CatalogoPrototipos", attrs=["-prototipos: Map<String, Producto>"],
      methods=["+registrar(clave, prototipo)","+crearVariante(clave, sku, talla, stock): Producto"]),
 node("Pedido", attrs=["-cliente: Cliente","-items: List<ItemPedido>","-estado: String"], methods=["+clone(): Pedido  // copia profunda"], fill=PAT),
 node("ItemPedido", attrs=["-producto: Producto","-cantidad: int"], methods=["+getSubtotal(): double"]),
 node("Cloneable","interface"),
], [
 edge("Polera","Producto","inherit"), edge("Pantalon","Producto","inherit"), edge("Zapatilla","Producto","inherit"),
 edge("Producto","Cloneable","realize"), edge("Pedido","Cloneable","realize"),
 edge("CatalogoPrototipos","Producto","agg","1  prototipos  *"),
 edge("Pedido","ItemPedido","comp","1  items  *"),
 edge("ItemPedido","Producto","assoc"),
])

# ---------- 3 Factory Method ----------
render("uml_factory", "Factory Method: ComprobanteFactory", [
 node("ComprobanteFactory","abstract",[],["#crearComprobante(): Comprobante {abstract}","+emitir(pedido, total): String","+de(tipo): ComprobanteFactory {static}"], fill=PAT),
 node("BoletaFactory", methods=["#crearComprobante(): Comprobante"], fill=PAT),
 node("FacturaFactory", methods=["#crearComprobante(): Comprobante"], fill=PAT),
 node("Comprobante","interface",[],["+generar(pedido, total): String"], fill=PROD),
 node("Boleta", attrs=["-CORRELATIVO: AtomicInteger {static}"], methods=["+generar(pedido, total): String"], fill=PROD),
 node("FacturaElectronica", attrs=["-CORRELATIVO: AtomicInteger {static}"], methods=["+generar(pedido, total): String"], fill=PROD),
], [
 edge("BoletaFactory","ComprobanteFactory","inherit"), edge("FacturaFactory","ComprobanteFactory","inherit"),
 edge("Boleta","Comprobante","realize"), edge("FacturaElectronica","Comprobante","realize"),
 edge("ComprobanteFactory","Comprobante","assoc"),
 edge("BoletaFactory","Boleta","depend","<<create>>"), edge("FacturaFactory","FacturaElectronica","depend","<<create>>"),
])

# ---------- 4 Abstract Factory ----------
nodes=[node("PasarelaPagoFactory","interface",[],["+crearMetodoPago(): IMetodoPago","+crearRecibo(): ReciboPago","+para(metodo): PasarelaPagoFactory {static}"], fill=PAT),
 node("IMetodoPago","interface",[],["+nombre(): String","+cobrar(monto): boolean"], fill=PROD),
 node("ReciboPago","interface",[],["+generar(monto): String"], fill=PROD)]
edges=[edge("PasarelaPagoFactory","IMetodoPago","assoc"), edge("PasarelaPagoFactory","ReciboPago","assoc")]
for n in ["Yape","Plin","Tarjeta"]:
    nodes += [node(n+"Factory", methods=["+crearMetodoPago()","+crearRecibo()"], fill=PAT),
              node(n+"Pago", methods=["+nombre()","+cobrar(monto)"], fill=PROD),
              node(n+"Recibo", methods=["+generar(monto)"], fill=PROD)]
    edges += [edge(n+"Factory","PasarelaPagoFactory","realize"), edge(n+"Pago","IMetodoPago","realize"),
              edge(n+"Recibo","ReciboPago","realize"),
              edge(n+"Factory",n+"Pago","depend","<<create>>"), edge(n+"Factory",n+"Recibo","depend","<<create>>")]
render("uml_abstractfactory", "Abstract Factory: familias de pasarelas de pago", nodes, edges)

# ---------- 5 Builder ----------
render("uml_builder", "Builder: Pedido.Builder", [
 node("Builder", attrs=["-cliente: Cliente  {obligatorio}","-items: List<ItemPedido>","-descuentoPct: double","-costoEnvio: double","-notas: String"],
      methods=["+Builder(cliente)","+agregarItem(producto, cantidad): Builder","+descuento(pct): Builder","+envio(costo): Builder","+notas(texto): Builder","+build(): Pedido"], fill=PAT),
 node("Pedido", attrs=["-cliente: Cliente","-items: List<ItemPedido>","-descuentoPct: double","-costoEnvio: double","-notas: String","-estado: String"],
      methods=["-Pedido(Builder)","+getSubtotal(): double","+clone(): Pedido"], fill=PROD),
 node("Cliente", attrs=["-dni: String","-nombres: String","-apellidos: String"], methods=["+getNombreCompleto(): String"]),
 node("ItemPedido", attrs=["-producto: Producto","-cantidad: int"], methods=["+getSubtotal(): double"]),
], [
 edge("Builder","Pedido","depend","<<create>>  build()"),
 edge("Builder","Cliente","assoc"), edge("Pedido","Cliente","assoc"),
 edge("Pedido","ItemPedido","comp","1  items  1..*"),
], rankdir="LR")

# ---------- 6 Adapter ----------
render("uml_adapter", "Adapter: ReniecAdapter", [
 node("VentaFacade", methods=["+registrarVenta(pedido, metodo, tipo): String"]),
 node("ValidadorDocumento","interface",[],["+validarDni(dni): boolean"], fill=PROD),
 node("ReniecAdapter", attrs=["-api: ReniecApiExterna"], methods=["+validarDni(dni): boolean"], fill=PAT),
 node("ReniecApiExterna", methods=["+consultarPersona(numero, tipo): String"], fill="#FDE7E7"),
], [
 edge("VentaFacade","ValidadorDocumento","assoc","usa (Target)"),
 edge("ReniecAdapter","ValidadorDocumento","realize"),
 edge("ReniecAdapter","ReniecApiExterna","assoc","adapta (Adaptee)"),
])

# ---------- 7 Facade ----------
render("uml_facade", "Facade: VentaFacade", [
 node("VentaFacade", attrs=["-validador: ValidadorDocumento","-inventario: InventarioService","-notificaciones: NotificacionService"],
      methods=["+registrarVenta(pedido, metodoPago, tipoComprobante): String"], fill=PAT),
 node("ValidadorDocumento","interface",[],["+validarDni(dni)"]),
 node("InventarioService", methods=["+verificarStock(pedido)","+descontarStock(pedido)"]),
 node("NotificacionService", methods=["+enviar(destinatario, mensaje)"]),
 node("PasarelaPagoFactory","interface",[],["+crearMetodoPago()","+crearRecibo()"]),
 node("ComprobanteFactory","abstract",[],["+emitir(pedido, total)"]),
 node("CalculoPrecio","interface",[],["+calcular()","+detalle()"]),
 node("Pedido", methods=["+getSubtotal()"]),
], [
 edge("VentaFacade","ValidadorDocumento","assoc"), edge("VentaFacade","InventarioService","assoc"),
 edge("VentaFacade","NotificacionService","assoc"),
 edge("VentaFacade","PasarelaPagoFactory","depend"), edge("VentaFacade","ComprobanteFactory","depend"),
 edge("VentaFacade","CalculoPrecio","depend"), edge("VentaFacade","Pedido","depend"),
], rankdir="TB")

# ---------- 8 Decorator ----------
render("uml_decorator", "Decorator: calculo del precio final", [
 node("CalculoPrecio","interface",[],["+calcular(): double","+detalle(): String"], fill=PROD),
 node("PrecioBase", attrs=["-monto: double"], methods=["+calcular(): double","+detalle(): String"], fill=PROD),
 node("DecoradorPrecio","abstract",["#envuelto: CalculoPrecio"],["+calcular(): double","+detalle(): String"], fill=PAT),
 node("ConDescuento", attrs=["-porcentaje: double"], methods=["+calcular(): double","+detalle(): String"], fill=PAT),
 node("ConIgv", methods=["+calcular(): double","+detalle(): String"], fill=PAT),
 node("ConEnvio", attrs=["-costo: double"], methods=["+calcular(): double","+detalle(): String"], fill=PAT),
], [
 edge("PrecioBase","CalculoPrecio","realize"), edge("DecoradorPrecio","CalculoPrecio","realize"),
 edge("DecoradorPrecio","CalculoPrecio","agg","envuelto"),
 edge("ConDescuento","DecoradorPrecio","inherit"), edge("ConIgv","DecoradorPrecio","inherit"), edge("ConEnvio","DecoradorPrecio","inherit"),
])

# ---------- 9 Composite ----------
render("uml_composite", "Composite: catalogo de categorias y productos", [
 node("ComponenteCatalogo","interface",[],["+getNombre(): String","+getPrecioTotal(): double","+getStockTotal(): int","+mostrar(sangria)"], fill=PROD),
 node("Producto","abstract",["-sku: String","-talla: String","-precio: double","-stock: int"],["+getPrecioTotal(): double","+getStockTotal(): int","+mostrar(sangria)"], fill=PAT),
 node("Categoria", attrs=["-nombre: String","-hijos: List<ComponenteCatalogo>"], methods=["+agregar(c)","+quitar(c)","+getPrecioTotal(): double","+getStockTotal(): int","+mostrar(sangria)"], fill=PAT),
 node("Polera", methods=[], fill=PROD), node("Pantalon", methods=[], fill=PROD), node("Zapatilla", methods=[], fill=PROD),
], [
 edge("Producto","ComponenteCatalogo","realize","Leaf"), edge("Categoria","ComponenteCatalogo","realize","Composite"),
 edge("Categoria","ComponenteCatalogo","comp","hijos  0..*"),
 edge("Polera","Producto","inherit"), edge("Pantalon","Producto","inherit"), edge("Zapatilla","Producto","inherit"),
])
print("ok")
