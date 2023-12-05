import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

public class CoordinationAgent extends Agent {
    private Map<String, String> ambulanceLocations = new HashMap<>();

    protected void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.INFORM) {
                        // Update ambulance location
                        updateAmbulanceLocation(msg.getContent());
                    } else if (msg.getPerformative() == ACLMessage.REQUEST) {
                        // Evaluate the emergency and dispatch the closest ambulance
                        dispatchClosestAmbulance(msg.getContent());
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void updateAmbulanceLocation(String locationMessage) {
        String[] parts = locationMessage.split(":");
        if (parts.length == 2) {
            String ambulanceName = parts[0].trim();
            String location = parts[1].trim();
            ambulanceLocations.put(ambulanceName, location);
        }
    }

    private void dispatchClosestAmbulance(String emergencyLocation) {
        System.out.println("Coordination Agent received emergency request at: " + emergencyLocation);

        // Print ambulance locations (for debugging)
        System.out.println("Available ambulance Locations:");
        for (Map.Entry<String, String> entry : ambulanceLocations.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }


        // Calculate the closest ambulance
       String closestAmbulance = calculateClosestAmbulance(emergencyLocation);

      // System.out.println("Closest Ambulance: " + closestAmbulance);

     if (closestAmbulance != null) {
            // Create a dispatch message
            ACLMessage dispatchMessage = new ACLMessage(ACLMessage.INFORM);
            dispatchMessage.setContent("Emergency at " + emergencyLocation + ". Dispatching " + closestAmbulance);
            System.out.println("closestAmbulance: "+closestAmbulance);
            dispatchMessage.addReceiver(getAID(closestAmbulance));

            // Send the dispatch message
            send(dispatchMessage);
        } else {
            System.out.println("No ambulance available for dispatch.");
        }
    }
    private String calculateClosestAmbulance(String emergencyLocation) {
        double emergencyLongitude = Double.parseDouble(emergencyLocation.split(",")[0]);
        double emergencyLatitude = Double.parseDouble(emergencyLocation.split(",")[1]);


        double minDistance = Double.MAX_VALUE;
        String closestAmbulance = null;

       for (Map.Entry<String, String> entry : ambulanceLocations.entrySet()) {

            String ambulanceName = entry.getKey();
            String ambulanceLocation = entry.getValue();
          // Extracting longitude and latitude using a more flexible approach
           String[] parts = ambulanceLocation.split(",");

           if (parts.length == 2) {
                try {
                    double ambulanceLongitude = Double.parseDouble(parts[0]);
                    double ambulanceLatitude = Double.parseDouble(parts[1]);

                    // Calculate Euclidean distance
                    double distance = Math.sqrt(Math.pow(emergencyLongitude - ambulanceLongitude, 2)
                            + Math.pow(emergencyLatitude - ambulanceLatitude, 2));

                    // Update closest ambulance if a closer one is found
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestAmbulance = ambulanceName;
                    }
                } catch (NumberFormatException e) {
                    // Handle the case where parsing longitude or latitude fails
                    System.err.println("Error parsing location for ambulance: " + ambulanceName);
                    e.printStackTrace();
                }
            }
        }

        return closestAmbulance;
    }

}
