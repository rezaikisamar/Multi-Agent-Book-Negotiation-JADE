package agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainContainer {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.GUI, "true");
            AgentContainer mc = rt.createMainContainer(p);

            for (int i = 1; i <= 10; i++) {
                AgentController bc = mc.createNewAgent("Biblio_"+i, "agents.BibliothequeAgent", null);
                bc.start();
            }

            Thread.sleep(2000);

            AgentController ec = mc.createNewAgent("Samar", "agents.EtudiantAgent", null);
            ec.start();

        } catch (Exception e) { e.printStackTrace(); }
    }
}