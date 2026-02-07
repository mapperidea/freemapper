import freemind.main.Resources
import freemind.modes.MindMapNode
import freemind.modes.ModeController

def props = Resources.getInstance().getProperties()
def savedNodeId = props.getProperty("mapperidea.temp.savedNodeId")
def savedNodeMapPath = props.getProperty("mapperidea.temp.savedNodeMapPath")
def currentMapPath = c.getMap().getFile() != null ? c.getMap().getFile().getAbsolutePath() : null

// --- LIMPEZA DE CONTEXTO ---
if (savedNodeId && savedNodeMapPath && currentMapPath && !savedNodeMapPath.equals(currentMapPath)) {
    props.remove("mapperidea.temp.savedNodeId")
    props.remove("mapperidea.temp.savedNodeMapPath")
    savedNodeId = null
}

if (savedNodeId) {
    // --- ESTADO 2: VOLTAR ---
    MindMapNode nodeToRestore = c.getNodeFromID(savedNodeId)
    if (nodeToRestore != null) {
        c.displayNode(nodeToRestore)
        c.centerNode(nodeToRestore)
        c.getFrame().out("Retornou ao nó original.")
    }
    props.remove("mapperidea.temp.savedNodeId")
    props.remove("mapperidea.temp.savedNodeMapPath")

} else {
    // --- ESTADO 1: BUSCAR E SALVAR ---
    MindMapNode selectedNode = c.getSelected()
    if (selectedNode == null) return

    def parent = selectedNode.getParentNode()
    String nodeText = selectedNode.getPlainTextContent()

    // Verifica se estamos sob um 'write-pattern'
    if (parent != null && parent.getPlainTextContent() == "write-pattern") {
        
        MindMapNode patternsRoot = findNodeByText(c.getRootNode(), "patterns")
        
        if (patternsRoot != null) {
            // Busca recursiva pelo texto em qualquer nível abaixo de 'patterns'
            MindMapNode targetNode = findNodeByText(patternsRoot, nodeText, true)
            
            if (targetNode != null && targetNode != patternsRoot) {
                // SÓ SALVA O CONTEXTO SE ENCONTRAR O DESTINO
                props.setProperty("mapperidea.temp.savedNodeId", selectedNode.getObjectId(c))
                props.setProperty("mapperidea.temp.savedNodeMapPath", currentMapPath ?: "unnamed_map")
                
                c.displayNode(targetNode)
                c.centerNode(targetNode)
                c.getFrame().out("Padrão encontrado e contexto salvo.")
            } else {
                c.getFrame().out("Alvo '" + nodeText + "' não encontrado em 'patterns'. Contexto não salvo.")
            }
        } else {
            c.getFrame().out("Nó raiz 'patterns' não encontrado.")
        }
    } else {
        // Comportamento padrão: se não for write-pattern, apenas salva o contexto normalmente
        props.setProperty("mapperidea.temp.savedNodeId", selectedNode.getObjectId(c))
        props.setProperty("mapperidea.temp.savedNodeMapPath", currentMapPath ?: "unnamed_map")
        c.getFrame().out("Contexto salvo (nó comum).")
    }
}

/**
 * Busca recursiva por um nó. 
 * @param excludeRoot Se true, não valida o próprio nó inicial da busca (útil para buscar filhos)
 */
MindMapNode findNodeByText(MindMapNode startNode, String text, boolean excludeRoot = false) {
    if (!excludeRoot && startNode.getPlainTextContent() == text) {
        return startNode
    }
    for (MindMapNode child : startNode.getChildren()) {
        def found = findNodeByText(child, text, false)
        if (found != null) return found
    }
    return null
}
