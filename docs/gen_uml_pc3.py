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

PROD = "#E8F0FE"; PAT = "#FFF4CE"; NEW = "#E3F6E8"

# ---------- Proxy ----------
render("uml_proxy", "Proxy: seguridad/auditoria de reportes y cache del catalogo", [
 node("ServicioReportes","interface",[],["+generar(tipo, formato): String"],fill=PROD),
 node("ServicioReportesReal",attrs=["-pedidos: PedidoDAO","-productos: ProductoDAO"],methods=["+generar(tipo, formato): String"],fill=PROD),
 node("ServicioReportesProxy",attrs=["-real: ServicioReportes","-usuario: Usuario","-auditoria: List<String>"],methods=["+generar(tipo, formato): String","-tienePermiso(tipo): boolean","+getAuditoria(): List<String>"],fill=PAT),
 node("ConsultaCatalogo","interface",[],["+buscarPorSku(sku): Optional<Producto>"],fill=PROD),
 node("CatalogoRepositorio",attrs=["-dao: ProductoDAO","-accesos: int"],methods=["+buscarPorSku(sku): Optional<Producto>"],fill=PROD),
 node("CatalogoCacheProxy",attrs=["-real: ConsultaCatalogo","-cache: Map<String, Producto>","-aciertos, fallos: int"],methods=["+buscarPorSku(sku): Optional<Producto>","+invalidar(sku)","+limpiar()"],fill=PAT),
 node("Usuario",attrs=["nombre: String","rol: Rol"],methods=[],fill=PROD),
], [
 edge("ServicioReportesReal","ServicioReportes","realize"),
 edge("ServicioReportesProxy","ServicioReportes","realize"),
 edge("ServicioReportesProxy","ServicioReportesReal","assoc","delega si hay permiso"),
 edge("ServicioReportesProxy","Usuario","depend","consulta el rol"),
 edge("CatalogoRepositorio","ConsultaCatalogo","realize"),
 edge("CatalogoCacheProxy","ConsultaCatalogo","realize"),
 edge("CatalogoCacheProxy","CatalogoRepositorio","assoc","delega en fallo de cache"),
], rankdir="BT")

# ---------- Bridge ----------
render("uml_bridge", "Bridge: Reporte (abstraccion) x ExportadorReporte (implementacion)", [
 node("Reporte","abstract",["#exportador: ExportadorReporte"],["+titulo(): String {abstract}","+columnas(): List<String> {abstract}","+filas(): List<List<String>> {abstract}","+setExportador(e)","+exportar(): String"],fill=PAT),
 node("ReporteVentas",attrs=["-pedidos: List<Pedido>"],methods=["+titulo()","+columnas()","+filas()"],fill=PROD),
 node("ReporteInventario",attrs=["-productos: List<Producto>"],methods=["+titulo()","+columnas()","+filas()"],fill=PROD),
 node("ExportadorReporte","interface",[],["+nombre(): String","+extension(): String","+exportar(titulo, columnas, filas): String","+para(formato): ExportadorReporte {static}"],fill=PAT),
 node("ExportadorHtml",methods=["+exportar(...)"],fill=PROD),
 node("ExportadorCsv",methods=["+exportar(...)"],fill=PROD),
 node("ExportadorJson",methods=["+exportar(...)"],fill=PROD),
], [
 edge("ReporteVentas","Reporte","inherit"),
 edge("ReporteInventario","Reporte","inherit"),
 edge("Reporte","ExportadorReporte","assoc","puente (delega el formato)"),
 edge("ExportadorHtml","ExportadorReporte","realize"),
 edge("ExportadorCsv","ExportadorReporte","realize"),
 edge("ExportadorJson","ExportadorReporte","realize"),
], rankdir="BT")

# ---------- State ----------
render("uml_state", "State: ciclo de vida del Pedido", [
 node("Pedido",attrs=["-estado: EstadoPedido"],methods=["+pagar()","+preparar()","+enviar()","+entregar()","+cancelar()","+cambiarEstado(nuevo)","+getEstado(): String"],fill=PAT),
 node("EstadoPedido","interface",[],["+nombre(): String","+pagar(p)","+preparar(p)","+enviar(p)","+entregar(p)","+cancelar(p)","+de(nombre): EstadoPedido {static}"],fill=PAT),
 node("EstadoBase","abstract",[],["+pagar(p): rechaza","+preparar(p): rechaza","+enviar(p): rechaza","+entregar(p): rechaza","+cancelar(p): rechaza"],fill=PROD),
 node("EstadoPendiente",methods=["+pagar(p) -> Pagado","+cancelar(p) -> Cancelado"],fill=PROD),
 node("EstadoPagado",methods=["+preparar(p) -> EnPreparacion","+cancelar(p) -> Cancelado"],fill=PROD),
 node("EstadoEnPreparacion",methods=["+enviar(p) -> Enviado","+cancelar(p) -> Cancelado"],fill=PROD),
 node("EstadoEnviado",methods=["+entregar(p) -> Entregado"],fill=PROD),
 node("EstadoEntregado",methods=["(estado final)"],fill=PROD),
 node("EstadoCancelado",methods=["(estado final)"],fill=PROD),
 node("TransicionInvalidaException",methods=[],fill=PROD),
], [
 edge("Pedido","EstadoPedido","assoc","estado actual"),
 edge("EstadoBase","EstadoPedido","realize"),
 edge("EstadoPendiente","EstadoBase","inherit"),
 edge("EstadoPagado","EstadoBase","inherit"),
 edge("EstadoEnPreparacion","EstadoBase","inherit"),
 edge("EstadoEnviado","EstadoBase","inherit"),
 edge("EstadoEntregado","EstadoBase","inherit"),
 edge("EstadoCancelado","EstadoBase","inherit"),
 edge("EstadoBase","TransicionInvalidaException","depend","lanza"),
], rankdir="BT")

# ---------- Observer ----------
render("uml_observer", "Observer: Pedido e InventarioService publican eventos", [
 node("Sujeto","abstract",["<<generico E>> tipo de evento","-observadores: List<Observador>"],["+suscribir(o)","+desuscribir(o)","#notificar(evento)","#reiniciarObservadores()"],fill=PAT),
 node("Observador","interface",["<<generico E>> tipo de evento"],["+actualizar(evento: E)"],fill=PAT),
 node("Pedido",methods=["+cambiarEstado(nuevo)  // notificar(CambioEstadoPedido)"],fill=PROD),
 node("InventarioService",methods=["+retirar(producto, cantidad)  // notificar(AlertaStock)","+reponer(producto, cantidad)"],fill=PROD),
 node("CambioEstadoPedido",attrs=["pedido: Pedido","estadoAnterior: String","estadoNuevo: String"],methods=[],fill=NEW),
 node("AlertaStock",attrs=["producto: Producto","stockActual: int","stockMinimo: int"],methods=[],fill=NEW),
 node("NotificadorClienteObserver",methods=["+actualizar(CambioEstadoPedido)"],fill=PROD),
 node("AuditoriaObserver",methods=["+actualizar(CambioEstadoPedido)"],fill=PROD),
 node("DashboardObserver",methods=["+actualizar(CambioEstadoPedido)"],fill=PROD),
 node("ComprasStockObserver",methods=["+actualizar(AlertaStock)"],fill=PROD),
], [
 edge("Pedido","Sujeto","inherit","E = CambioEstadoPedido"),
 edge("InventarioService","Sujeto","inherit","E = AlertaStock"),
 edge("Sujeto","Observador","assoc","0..*"),
 edge("NotificadorClienteObserver","Observador","realize"),
 edge("AuditoriaObserver","Observador","realize"),
 edge("DashboardObserver","Observador","realize"),
 edge("ComprasStockObserver","Observador","realize"),
 edge("Pedido","CambioEstadoPedido","depend","publica"),
 edge("InventarioService","AlertaStock","depend","publica"),
], rankdir="RL")

# ---------- Command ----------
render("uml_command", "Command: operaciones de inventario reversibles y auditables", [
 node("Comando","interface",[],["+ejecutar()","+deshacer()","+descripcion(): String"],fill=PAT),
 node("DescontarStockComando",attrs=["-inventario: InventarioService","-producto: Producto","-cantidad: int"],methods=["+ejecutar()  // retirar","+deshacer()  // reponer"],fill=PROD),
 node("ReponerStockComando",attrs=["-inventario: InventarioService","-producto: Producto","-cantidad: int"],methods=["+ejecutar()  // reponer","+deshacer()  // retirar"],fill=PROD),
 node("ComandoCompuesto",attrs=["-hijos: List<Comando>"],methods=["+ejecutar()  // atomico","+deshacer()"],fill=PROD),
 node("InvocadorComandos",attrs=["-hechos, deshechos: Deque<Comando>","-cola: Deque<Comando>","-bitacora: List<String>"],methods=["+ejecutar(c)","+deshacer(): boolean","+rehacer(): boolean","+encolar(c)","+procesarCola(): int"],fill=PAT),
 node("InventarioService",methods=["+retirar(producto, cantidad)","+reponer(producto, cantidad)"],fill=PROD),
], [
 edge("DescontarStockComando","Comando","realize"),
 edge("ReponerStockComando","Comando","realize"),
 edge("ComandoCompuesto","Comando","realize"),
 edge("ComandoCompuesto","Comando","agg","hijos"),
 edge("InvocadorComandos","Comando","assoc","ejecuta"),
 edge("DescontarStockComando","InventarioService","assoc","receptor"),
 edge("ReponerStockComando","InventarioService","assoc","receptor"),
], rankdir="BT")

# ---------- Memento ----------
render("uml_memento", "Memento: deshacer/rehacer en el borrador del pedido", [
 node("BorradorPedido",attrs=["-cliente: Cliente","-items: List<ItemPedido>","-descuentoPct, costoEnvio: double","-notas: String"],methods=["+agregarItem(p, n)","+quitarItem(sku)","+descuento(pct)","+guardar(): Memento","+restaurar(m: Memento)","+confirmar(): Pedido"],fill=PAT),
 node("Memento",attrs=["-items: List<ItemPedido>","-descuentoPct: double","-costoEnvio: double","-notas: String"],methods=["(sin metodos publicos)"],fill=PAT),
 node("HistorialBorrador",attrs=["-paraDeshacer: Deque<Memento>","-paraRehacer: Deque<Memento>"],methods=["+respaldar(b)","+deshacer(b): boolean","+rehacer(b): boolean"],fill=PAT),
 node("Pedido",methods=[],fill=PROD),
], [
 edge("BorradorPedido","Memento","comp","crea y restaura"),
 edge("HistorialBorrador","Memento","agg","guarda (sin abrirlos)"),
 edge("HistorialBorrador","BorradorPedido","depend","respalda / restaura"),
 edge("BorradorPedido","Pedido","depend","confirmar() con Builder"),
], rankdir="TB")

# ---------- Template Method (refactor Copy-Paste) ----------
render("uml_refactor_templatemethod", "Refactor Copy-Paste: Template Method en pagos, recibos y comprobantes", [
 node("IMetodoPago","interface",[],["+nombre(): String","+cobrar(monto): boolean"],fill=PROD),
 node("PagoConLimite","abstract",["-nombre: String","-limite: double"],["+nombre(): String","+cobrar(monto): boolean  // una sola vez"],fill=PAT),
 node("YapePago",methods=["super(\"Yape\", 500)"],fill=PROD),
 node("PlinPago",methods=["super(\"Plin\", 500)"],fill=PROD),
 node("TarjetaPago",methods=["super(\"Tarjeta\", 5000)"],fill=PROD),
 node("Comprobante","interface",[],["+generar(pedido, total): String"],fill=PROD),
 node("ComprobanteBase","abstract",["-CORRELATIVOS: Map<String, AtomicInteger> {static}"],["+generar(pedido, total): String  // plantilla","#titulo(): String {abstract}","#serie(): String {abstract}","#descripcionCliente(p): String {abstract}"],fill=PAT),
 node("Boleta",methods=["#titulo() / #serie() / #descripcionCliente()"],fill=PROD),
 node("FacturaElectronica",methods=["#titulo() / #serie() / #descripcionCliente()"],fill=PROD),
], [
 edge("PagoConLimite","IMetodoPago","realize"),
 edge("YapePago","PagoConLimite","inherit"),
 edge("PlinPago","PagoConLimite","inherit"),
 edge("TarjetaPago","PagoConLimite","inherit"),
 edge("ComprobanteBase","Comprobante","realize"),
 edge("Boleta","ComprobanteBase","inherit"),
 edge("FacturaElectronica","ComprobanteBase","inherit"),
], rankdir="BT")

# ---------- Arquitectura integrada ----------
render("uml_arquitectura", "Arquitectura en capas e integracion de los patrones (PC2 + PC3)", [
 node("DemoGUI_y_Paneles",attrs=["Presentacion (Swing)"],methods=["solo llama al controlador"],fill=PROD),
 node("VentaController",attrs=["Controladores"],methods=["+registrar(SolicitudVenta)","+crearPedido(...)","+nuevoBorrador(...)"],fill=PROD),
 node("VentaFacade",attrs=["Logica de negocio"],methods=["+registrarVenta(pedido, pago, comprobante)"],fill=PAT),
 node("Servicios",attrs=["InventarioService (Observer)","NotificacionService","ServicioReportesProxy (Proxy + Bridge)","InvocadorComandos (Command)"],methods=[],fill=PAT),
 node("Dominio",attrs=["Pedido (State + Observer + Builder + Prototype)","Producto (Prototype + Composite)","BorradorPedido (Memento)"],methods=[],fill=PAT),
 node("DAO",attrs=["ProductoDAO / PedidoDAO","Memoria | JDBC MySQL (ConexionBD, Singleton)"],methods=[],fill=PROD),
 node("ContextoTienda",attrs=["Raiz de composicion"],methods=["inyecta dependencias por constructor"],fill=NEW),
], [
 edge("DemoGUI_y_Paneles","VentaController","depend"),
 edge("VentaController","VentaFacade","depend"),
 edge("VentaFacade","Servicios","depend"),
 edge("VentaFacade","Dominio","depend"),
 edge("Servicios","DAO","depend"),
 edge("VentaFacade","DAO","depend"),
 edge("ContextoTienda","VentaController","depend","crea"),
], rankdir="TB")
