package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
//C'est l'agent qui pilote la recherche et l'achat. Son intelligence est séquentielle
public class EtudiantAgent extends Agent {
    private AgentWindow window;
    private List<AID> vendeursDisponibles = new ArrayList<>();

    protected void setup() {
        window = new AgentWindow("ÉTUDIANT : " + getLocalName(), Color.GREEN, true);// etudiante samar
        window.addLog("1. Entrez le nom du livre et vérifiez la disponibilité.");// f la console

        // ACTION DU BOUTON : ÉTAPE 1
        window.setButtonAction(e -> verifierDisponibilite());
    }

    private void verifierDisponibilite() {
        String livre = window.getInput();
        if (livre.isEmpty()) return;

        window.addLog("--- ÉTAPE 1 : DISPONIBILITÉ ---");//affichage lawel
        vendeursDisponibles.clear();
        try {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("vente-livres");
            template.addServices(sd);
            DFAgentDescription[] result = DFService.search(this, template);
            // dans Directory Facilitator tous les agents "vente-livres",
            //ACL (Agent Communication Language)
            for (DFAgentDescription dfd : result) {
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);//Call For Proposal(Est-ce que tu as ce livre)
               // requête de disponibilité de l'étudiant vers une bibliothèque
                msg.addReceiver(dfd.getName());//recupere identifiant de biblio li f pages jaunes
                msg.setContent(livre);//difinit contenue te3 msg 
                send(msg);//nb3to msg
                ACLMessage rep = blockingReceive();
                //leur envoie un CFP et stocke ceux qui répondent "OUI"
                if (rep.getPerformative() == ACLMessage.PROPOSE) {
                    window.addLog(dfd.getName().getLocalName() + " : OUI");//format te3 msg Biblio_2 : OUI
                    vendeursDisponibles.add(dfd.getName());
                } else {
                    window.addLog(dfd.getName().getLocalName() + " : NON");
                }
            }
            
            if (!vendeursDisponibles.isEmpty()) {
                window.setButtonText("2. Demander les Prix");//action 2
                window.setButtonAction(ev -> demanderPrix(livre));//3amlya hedi nhws 3la prix 
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void demanderPrix(String livre) {
        window.addLog("--- ÉTAPE 2 : DEMANDE DES PRIX ---");// wesh teny msg tkhrjo 
        for (AID biblio : vendeursDisponibles) {
            ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);//  requête de prix initial de l'étudiant vers les bibliothèques
            msg.addReceiver(biblio);
            msg.setContent(livre);
            send(msg);//yb3t msg
            ACLMessage rep = blockingReceive();
            window.addLog(biblio.getLocalName() + " propose : " + rep.getContent() + " DA");//forme te3 msg >>Biblio_2 propose : 450.3293591430852 DA
        }
        window.setButtonText("3. Demander Réduction");//action 3
        window.setButtonAction(ev -> demanderReduction(livre));
    }

    private void demanderReduction(String livre) {
        window.addLog("--- ÉTAPE 3 : NÉGOCIATION ---");//msg telt
        AID meilleurVendeur = null;// flwl null
        double meilleurPrix = Double.MAX_VALUE;//ykhyr 3ali ga3

        for (AID biblio : vendeursDisponibles) {
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);// requête de reduction de l'étudiant vers une bibliothèque
            msg.addReceiver(biblio);
            msg.setContent(livre);
            send(msg);
            ACLMessage rep = blockingReceive();
            double prixFinal = Double.parseDouble(rep.getContent());//prix be3d reduction
            window.addLog(biblio.getLocalName() + " après réduc : " + prixFinal + " DA"); //msg >>  Biblio_2 après réduc : 405.2964232287767 DA
//l'intelligence décisionnelle de l'étudiant. 
//C'est ici qu'il compare toutes les offres
            if (prixFinal < meilleurPrix) {
                meilleurPrix = prixFinal;
                meilleurVendeur = biblio;
            }
        }
        
        final AID gagnant = meilleurVendeur;//stock resultat hna
        final double prixGagnant = meilleurPrix;//stock resultat hna
        window.setButtonText("4. Acheter au moins cher");// action 4
        window.setButtonAction(ev -> finaliserAchat(livre, gagnant, prixGagnant));//y3yt l hedi
    }

    private void finaliserAchat(String livre, AID gagnant, double prix) {
        window.addLog("--- ÉTAPE 4 : ACHAT AUTOMATIQUE ---");//msg rab3
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);//hna tedi accepta proposal te3 meilleur vendeur
        msg.addReceiver(gagnant);//meilleur vendeur
        msg.setContent(livre);
        send(msg);//envoie accept_proposal ,biliotheque recoit msg 
        //afficher "achat reussi", biblio decrementer 
        window.addLog("ACHAT RÉUSSI chez " + gagnant.getLocalName() + " pour " + prix + " DA");// msg << ACHAT RÉUSSI chez Biblio_7 pour 183.00861245705363 DA
        
        // Reset pour une nouvelle recherche w biblio tktb vendu 
        window.setButtonText("1. Vérifier Disponibilité");
        window.setButtonAction(e -> verifierDisponibilite());
    }
}