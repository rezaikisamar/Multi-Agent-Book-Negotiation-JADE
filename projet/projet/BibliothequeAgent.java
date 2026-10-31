package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
//Cet agent représente une bibliothèque qui possède un stock de livres.
public class BibliothequeAgent extends Agent {
    private Map<String, double[]> catalogue = new HashMap<>();//yst3l hashmap cle howa titre 
    private AgentWindow window;

    protected void setup() {//cree sa fenetre 
        window = new AgentWindow(getLocalName(), Color.WHITE, false);
        initialiserCatalogue();//mn te7t bayn kifesh yji dayr
        window.addLog("Agent " + getLocalName() + " prêt.");//exemple agent biblio_3 pret

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("vente-livres");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);//Il s'enregistre dans les Pages Jaunes
        } catch (FIPAException fe) { fe.printStackTrace(); }

        addBehaviour(new CyclicBehaviour() {//Il attend des messages en boucle et utilise un switch sur le type de message (Performative)
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String livreCherche = msg.getContent();
                    ACLMessage reply = msg.createReply();

                    switch (msg.getPerformative()) {
                        case ACLMessage.CFP: // ÉTAPE 1 :  Rechercher la DISPO
                            if (catalogue.containsKey(livreCherche) && catalogue.get(livreCherche)[1] > 0) {
                                window.addLog("Livre '" + livreCherche + "' demandé. État : DISPONIBLE");//  tkhrj hek ida dispomible
                                reply.setPerformative(ACLMessage.PROPOSE);
                            } else {
                                window.addLog("Livre '" + livreCherche + "' demandé. État : ÉPUISÉ");
                                reply.setPerformative(ACLMessage.REFUSE);
                            }
                            break;

                        case ACLMessage.QUERY_IF: // ÉTAPE 2 : tb3et PRIX INITIAL
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent(String.valueOf(catalogue.get(livreCherche)[0]));//yb3t prix
                            break;

                        case ACLMessage.PROPOSE: // ÉTAPE 3 :  appliquer la RÉDUCTION automatique 
                            double prixReduit = catalogue.get(livreCherche)[0] * 0.90;
                            window.addLog("Négociation : prix réduit à " + String.format("%.2f", prixReduit));// msg f biblio
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent(String.valueOf(prixReduit));
                            break;

                        case ACLMessage.ACCEPT_PROPOSAL: // ÉTAPE 4 :  L'ACHAT (recoit msg accept_proposal)
                            double[] infos = catalogue.get(livreCherche);
                            infos[1] = infos[1] - 1; //  decrementer Stock - 1
                            catalogue.put(livreCherche, infos);
                            window.addLog(">>> VENDU ! Stock actuel : " + (int)infos[1]);// ykhrj msg heda f biblio li ba3t be3 msg te3 achat reussi te3 etudiant
                            reply.setPerformative(ACLMessage.INFORM);
                            break;
                    }
                    send(reply);
                } else { block(); }
            }
        });
    }

    private void initialiserCatalogue() {
        ajouterLivre("Java", 200, 500);
        ajouterLivre("Python", 150, 450);
        ajouterLivre("Reseaux", 300, 600);
        ajouterLivre("SMA", 400, 850);
        ajouterLivre("Algorithmique", 100, 300);
        ajouterLivre("PHP", 200, 400);
    }

    private void ajouterLivre(String titre, double min, double max) {
        double prix = min + (Math.random() * (max - min));
        int quantite = (int)(Math.random() * 6); 
        catalogue.put(titre, new double[]{prix, quantite});
        window.addLog(titre + " | Stock: " + quantite);// heka rayh yban livre f kol agent bibliotheque 
    }
}