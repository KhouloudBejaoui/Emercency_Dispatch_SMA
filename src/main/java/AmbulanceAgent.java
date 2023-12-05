import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AmbulanceAgent extends Agent {
    private double longitude;
    private double latitude;

    protected void setup() {
        this.longitude = Math.random() * 10;
        this.latitude = Math.random() * 10;

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    handleEmergencyRequest(msg.getContent());
                } else {
                    sendLocation();
                    block();
                }
            }
        });
    }

    private void handleEmergencyRequest(String emergencyLocation) {
        System.out.println(getLocalName() + " received emergency request: " + emergencyLocation);
    }

    private void sendLocation() {
        ACLMessage locationMessage = new ACLMessage(ACLMessage.INFORM);
        locationMessage.setContent(getLocalName() + ": " + longitude + "," + latitude);
        locationMessage.addReceiver(getAID("Coordination"));

        send(locationMessage);
    }
}
