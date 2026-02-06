import freemind.main.Resources
import freemind.modes.MindMapNode
import freemind.modes.ModeController // Para o getNodeFromID
import javax.swing.JOptionPane
import java.util.ArrayList // Para o displayNode, se necessário

// A variável 'c' (MindMapController) é injetada automaticamente.
// Ela estende ModeController, então pode ser usada para getNodeFromID.

def props = Resources.getInstance().getProperties()
def savedNodeId = props.getProperty("mapperidea.temp.savedNodeId")
def savedNodeMapPath = props.getProperty("mapperidea.temp.savedNodeMapPath") // Caminho do mapa onde o nó foi salvo
def currentMapPath = c.getMap().getFile() != null ? c.getMap().getFile().getAbsolutePath() : null

// Verifica se o contexto foi salvo em outro mapa
if (savedNodeId && savedNodeMapPath && currentMapPath && !savedNodeMapPath.equals(currentMapPath)) {
    c.getFrame().out("Contexto salvo em outro mapa: " + savedNodeMapPath + ". Limpando contexto.")
    props.remove("mapperidea.temp.savedNodeId")
    props.remove("mapperidea.temp.savedNodeMapPath")
    savedNodeId = null
}

if (savedNodeId) {
    // --- ESTADO 2: CONTEXTO SALVO, RESTAURAR ---
    
    // Tenta obter o nó usando o ID
    MindMapNode nodeToRestore = c.getNodeFromID(savedNodeId)
    
    if (nodeToRestore != null) {
        // Assegura que o nó esteja visível (desdobrando o caminho até ele)
        // c.displayNode(node) já faz isso e centraliza
        c.displayNode(nodeToRestore)
        c.centerNode(nodeToRestore) // Centraliza o nó na tela
        c.getFrame().out("Contexto restaurado para: " + nodeToRestore.getPlainTextContent())
    } else {
        c.getFrame().out("Nó salvo não encontrado no mapa atual. O contexto foi limpo.")
    }
    
    // Limpa o estado para a próxima execução
    props.remove("mapperidea.temp.savedNodeId")
    props.remove("mapperidea.temp.savedNodeMapPath")

} else {
    // --- ESTADO 1: NENHUM CONTEXTO, SALVAR ---
    
    MindMapNode selectedNode = c.getSelected()
    if (selectedNode != null) {
        String nodeId = selectedNode.getObjectId(c)
        String nodeText = selectedNode.getPlainTextContent()
        
        if (nodeId != null && !nodeId.isEmpty()) {
            // Salva o ID do nó e o caminho do mapa nas propriedades do usuário
            props.setProperty("mapperidea.temp.savedNodeId", nodeId)
            props.setProperty("mapperidea.temp.savedNodeMapPath", currentMapPath != null ? currentMapPath : "unnamed_map")
            
            // Exibe feedback na barra de status
            String statusMessage = "Contexto salvo: " + (nodeText.length() > 50 ? nodeText.substring(0, 50) + "..." : nodeText)
            c.getFrame().out(statusMessage)
        } else {
             c.getFrame().out("Não foi possível obter um ID único para o nó selecionado. Contexto não salvo.")
        }
    } else {
        c.getFrame().out("Nenhum nó selecionado para salvar o contexto.")
    }
}