import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class EncontroCarros {
    private JFrame frame;
    private JTextField vAField, aAField, xAField;
    private JTextField vBField, aBField, xBField;
    private JTextField tempoField;
    private JTextArea outputArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EncontroCarros().criarInterface());
    }

    private void criarInterface() {
        frame = new JFrame("Calculadora de Encontro de Carros");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel panel = new JPanel(new GridLayout(10, 3, 5, 5));

        vAField = criarCampo(panel, "Velocidade de A (m/s):");
        aAField = criarCampo(panel, "Aceleração de A (m/s²):");
        xAField = criarCampo(panel, "Posição inicial de A (m):");

        vBField = criarCampo(panel, "Velocidade de B (m/s):");
        aBField = criarCampo(panel, "Aceleração de B (m/s²):");
        xBField = criarCampo(panel, "Posição inicial de B (m):");

        tempoField = criarCampo(panel, "Tempo até a colisão (s):");

        JButton calcularButton = new JButton("Calcular");
        calcularButton.addActionListener(e -> resolver());
        panel.add(calcularButton);

        JButton limparButton = new JButton("Limpar");
        limparButton.addActionListener(e -> limparCampos());
        panel.add(limparButton);

        outputArea = new JTextArea(5, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(scrollPane, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private JTextField criarCampo(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        JTextField field = new JTextField();
        panel.add(label);
        panel.add(field);
        return field;
    }

    private void limparCampos() {
        vAField.setText(""); aAField.setText(""); xAField.setText("");
        vBField.setText(""); aBField.setText(""); xBField.setText("");
        tempoField.setText("");
        outputArea.setText("");
    }

    private void resolver() {
        try {
            Double vA = parse(vAField.getText());
            Double aA = parse(aAField.getText());
            Double xA = parse(xAField.getText());

            Double vB = parse(vBField.getText());
            Double aB = parse(aBField.getText());
            Double xB = parse(xBField.getText());

            Double tempo = parse(tempoField.getText());

            List<String> faltando = new ArrayList<>();
            if (tempo == null) faltando.add("tempo até a colisão");

            Double posA = null, posB = null;
            if (xA != null && vA != null && aA != null && tempo != null)
                posA = xA + vA * tempo + 0.5 * aA * tempo * tempo;
            else if (xA != null && vA != null && tempo != null && aA == null)
                posA = xA + vA * tempo;
            else faltando.add("posição de A");

            if (xB != null && vB != null && aB != null && tempo != null)
                posB = xB + vB * tempo + 0.5 * aB * tempo * tempo;
            else if (xB != null && vB != null && tempo != null && aB == null)
                posB = xB + vB * tempo;
            else faltando.add("posição de B");

            if (posA != null && posB != null && Math.abs(posA - posB) < 1e-2) {
                outputArea.setText("Carros se encontram na posição: " + String.format("%.2f", posA) + " m");
            } else if (!faltando.isEmpty()) {
                outputArea.setText("Impossível calcular. Forneça: " + String.join(", ", faltando));
            } else {
                outputArea.setText("Os carros não se encontram com os dados fornecidos.");
            }

        } catch (Exception e) {
            outputArea.setText("Erro: Entrada inválida.");
        }
    }

    private Double parse(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        return Double.parseDouble(text.trim());
    }
}