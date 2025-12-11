// Mapper Idea Contextual Generator for FreeMind
// Translated and adapted from the Freeplane version by the Gemini assistant.

import freemind.modes.MindMapNode
import freemind.modes.MindIcon

// Get the currently selected node from the controller.
def selectedNode = c.getSelected()

if (selectedNode == null) {
    c.getFrame().out("Por favor, selecione um nó para executar o gerador contextual.")
    return
}

def nodeName = selectedNode.getText().trim()

/**
 * Creates a new child node if a child with the same text doesn't already exist.
 * This function is adapted for the FreeMind API, using the Controller 'c' for all modifications.
 *
 * @param parentNode The node to which the child will be added.
 * @param text The text for the new child node.
 * @param iconKey A key ('e' for element, 'v' for value) to determine which icon to add.
 * @return The newly created child node, or the existing one if found.
 */
def createNodeIfMissing(MindMapNode parentNode, String text, String iconKey) {
    // Check if a child with the same text already exists.
    def existingChild = parentNode.getChildren().find { it.getText() == text }
    if (existingChild) {
        return existingChild // It already exists, do nothing.
    }

    // Create the new node using the controller.
    // Index -1 adds the node at the end of the children list.
    def child = c.addNewNode(parentNode, -1, false)
    // Set the text for the new node using the controller.
    c.setNodeText(child, text)

    // Add icons based on the Mapper Idea grammar using the controller.
    if (iconKey == "e") {
        // Icon for an element, like <mapperidea>, <generators>, etc.
        c.addIcon(child, MindIcon.factory("element"))
    } else if (iconKey == "v") {
        // Icon for a value, like a className or a pattern name.
        c.addIcon(child, MindIcon.factory("tag_green")) // Using tag_green as a more semantic value icon.
    }
    return child
}

// Decision logic based on the Mapper Idea hierarchy.
// This switch statement determines which children to create based on the selected node's text.
switch (nodeName) {

    case "config":
        createNodeIfMissing(selectedNode, "mapperidea", "e")
        break

    case "mapperidea":
        // Global structures
        createNodeIfMissing(selectedNode, "generators", "e")
        createNodeIfMissing(selectedNode, "injectables", "e")
        createNodeIfMissing(selectedNode, "maps", "e")
        createNodeIfMissing(selectedNode, "fragments", "e")
        break

    case "generators":
        // Suggest a generic generator name for the user to rename.
        createNodeIfMissing(selectedNode, "meuGerador", "e")
        break

    // Generic detection for a specific generator (e.g., 'sql', 'java').
    // Assumes that if the parent is 'generators', this node is a Generator.
    case { it != "generators" && selectedNode.getParent() != null && selectedNode.getParent().getText() == "generators" }:
        createNodeIfMissing(selectedNode, "parameters", "e")
        createNodeIfMissing(selectedNode, "vars", "e")
        createNodeIfMissing(selectedNode, "start", "e")
        createNodeIfMissing(selectedNode, "templates", "e")
        createNodeIfMissing(selectedNode, "patterns", "e")
        break
        
    case "parameters":
        def classNameNode = createNodeIfMissing(selectedNode, "className", "e")
        createNodeIfMissing(classNameNode, "NOT_DEFINED", "v")
        break

    case "start":
    case "template":
        // Flow start structure
        createNodeIfMissing(selectedNode, "match", "e")
        createNodeIfMissing(selectedNode, "body", "e")
        break

    case "match":
         // XPath suggestion
         createNodeIfMissing(selectedNode, "classes/class", "v")
         break
         
    case "body":
    case "then":
    case "else":
        // Execution commands
        createNodeIfMissing(selectedNode, "write-pattern", "e")
        createNodeIfMissing(selectedNode, "apply-templates", "e")
        createNodeIfMissing(selectedNode, "if", "e")
        createNodeIfMissing(selectedNode, "vars", "e")
        break

    case "apply-templates":
        // Loop/application structure
        createNodeIfMissing(selectedNode, "select", "e")
        createNodeIfMissing(selectedNode, "mode", "e")
        createNodeIfMissing(selectedNode, "parameters", "e")
        break

    case "select":
        createNodeIfMissing(selectedNode, "attributes/attribute", "v")
        break

    case "write-pattern":
        // Reference to a pattern
        createNodeIfMissing(selectedNode, "nomeDoPadrao", "v")
        break
        
    case "patterns":
        createNodeIfMissing(selectedNode, "nomeDoPadrao", "e")
        break

    default:
        c.getFrame().out("Nenhuma sugestão automática configurada para o nó: " + nodeName)
}

// Give focus back to the map view
c.getView().requestFocus()
