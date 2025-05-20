import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

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

            if (tempo == null) {
                faltando.add("tempo até a colisão");
            } else {
                // Tentar calcular variáveis ausentes

                // 1. Tentar calcular xA
                if (xA == null && xB != null && vA != null && aA != null && vB != null && aB != null) {
                    xA = xB + vB * tempo + 0.5 * aB * tempo * tempo - vA * tempo - 0.5 * aA * tempo * tempo;
                    xAField.setText(String.format("%.2f", xA));
                }
                // 2. Tentar calcular xB
                if (xB == null && xA != null && vA != null && aA != null && vB != null && aB != null) {
                    xB = xA + vA * tempo + 0.5 * aA * tempo * tempo - vB * tempo - 0.5 * aB * tempo * tempo;
                    xBField.setText(String.format("%.2f", xB));
                }
                // 3. Tentar calcular vA
                if (vA == null && xA != null && aA != null && xB != null && vB != null && aB != null) {
                    vA = (xB + vB * tempo + 0.5 * aB * tempo * tempo - xA - 0.5 * aA * tempo * tempo) / tempo;
                    vAField.setText(String.format("%.2f", vA));
                }
                // 4. Tentar calcular vB
                if (vB == null && xB != null && aB != null && xA != null && vA != null && aA != null) {
                    vB = (xA + vA * tempo + 0.5 * aA * tempo * tempo - xB - 0.5 * aB * tempo * tempo) / tempo;
                    vBField.setText(String.format("%.2f", vB));
                }
                // 5. Tentar calcular aA
                if (aA == null && xA != null && vA != null && xB != null && vB != null && aB != null) {
                    aA = (xB + vB * tempo + 0.5 * aB * tempo * tempo - xA - vA * tempo) / (0.5 * tempo * tempo);
                    aAField.setText(String.format("%.4f", aA));
                }
                // 6. Tentar calcular aB
                if (aB == null && xB != null && vB != null && xA != null && vA != null && aA != null) {
                    aB = (xA + vA * tempo + 0.5 * aA * tempo * tempo - xB - vB * tempo) / (0.5 * tempo * tempo);
                    aBField.setText(String.format("%.4f", aB));
                }
            }

            Double posA = null, posB = null;
            if (xA != null && vA != null && aA != null && tempo != null)
                posA = xA + vA * tempo + 0.5 * aA * tempo * tempo;
            else faltando.add("posição de A");

            if (xB != null && vB != null && aB != null && tempo != null)
                posB = xB + vB * tempo + 0.5 * aB * tempo * tempo;
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