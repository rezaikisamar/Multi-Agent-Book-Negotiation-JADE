package agents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AgentWindow extends JFrame {//Composants
    private JTextArea logArea;//pour afficher l'historique des actions
    private JTextField inputField;//pour la saisie
    private JButton actionButton;//pour les actions.

    public AgentWindow(String title, Color color, boolean isEtudiant) {
        setTitle(title);
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(color);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        if (isEtudiant) {//eh wela nn  décide ida affiche les champs de saisie
            JPanel panel = new JPanel(new GridLayout(2, 1));
            inputField = new JTextField();
            actionButton = new JButton("1. Vérifier Disponibilité");
            
            panel.add(new JLabel(" Nom du livre à chercher :"));
            panel.add(inputField);
            add(panel, BorderLayout.NORTH);
            add(actionButton, BorderLayout.SOUTH);
        }
        setVisible(true);
    }

    public void addLog(String message) {//Ajoute une ligne texte f la console de l'agent
        logArea.append("> " + message + "\n");
    }

    public String getInput() { return inputField.getText(); }
    
    public void setButtonAction(ActionListener al) {//Permet de changer dynamiquement ce que fait le bouton
        // Supprime les anciens événements avant d'ajouter le nouveau
        for (ActionListener l : actionButton.getActionListeners()) {
            actionButton.removeActionListener(l);
        }
        actionButton.addActionListener(al);
    }
    
    public void setButtonText(String text) { actionButton.setText(text); }
}