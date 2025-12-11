// As variáveis 'c' (Controller) e 'node' (MindMapNode, mas é o root) são injetadas.
// Vamos ignorar a variável 'node' e usar c.getSelected() para pegar a seleção atual.
def selectedNode = c.getSelected()

if (selectedNode != null) {
    println "Executando script 'Listar Filhos' no nó: '${selectedNode.getText()}'"
    def children = selectedNode.getChildren()
    if (children.isEmpty()) {
        println "Este nó não tem filhos."
        // Opcional: Mostrar uma mensagem para o usuário
        javax.swing.JOptionPane.showMessageDialog(null, "O nó '${selectedNode.getText()}' não tem filhos.")
    } else {
        def childList = "Filhos do nó '${selectedNode.getText()}':\n"
        println "Filhos do nó:"
        children.each { child ->
            println "- ${child.getText()}"
            childList += "- ${child.getText()}\n"
        }
        // Opcional: Mostrar uma mensagem para o usuário
        javax.swing.JOptionPane.showMessageDialog(null, childList)
    }
} else {
    println "Nenhum nó selecionado."
    javax.swing.JOptionPane.showMessageDialog(null, "Por favor, selecione um nó primeiro.")
}
