import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class EmergencyCallerAgent extends Agent {
    private double emergencyLongitude;
    private double emergencyLatitude;

    protected void setup() {
        this.emergencyLongitude = Math.random() * 10;  // Replace with actual coordinates
        this.emergencyLatitude = Math.random() * 10;   // Replace with actual coordinates

        addBehaviour(new OneShotBehaviour(this) {
            public void action() {
                System.out.println(getLocalName() + " is making an emergency call to coordinates: "
                        + emergencyLongitude + "," + emergencyLatitude);

                ACLMessage emergencyMessage = new ACLMessage(ACLMessage.REQUEST);
                emergencyMessage.setContent(emergencyLongitude + "," + emergencyLatitude);
                emergencyMessage.addReceiver(getAID("Coordination"));

                send(emergencyMessage);
            }
        });
    }
}
