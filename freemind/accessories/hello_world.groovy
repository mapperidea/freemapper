import javax.swing.JOptionPane

// A variável 'c' é o MindMapController, injetado automaticamente.
// Usamos c.getSelected() para pegar o nó que o usuário selecionou.
def selectedNode = c.getSelected()

if (selectedNode != null) {
    def nodeText = selectedNode.getText()
    JOptionPane.showMessageDialog(null, "O nó selecionado é: " + nodeText)
} else {
    JOptionPane.showMessageDialog(null, "Nenhum nó selecionado.")
}
