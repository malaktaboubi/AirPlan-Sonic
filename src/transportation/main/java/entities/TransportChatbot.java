package entities;

import services.ServiceTransportation;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TransportChatbot {
    private enum State {
        INITIAL,
        COLLECTING_DATA,
        COMPLETED
    }

    private State currentState = State.INITIAL;
    private Transportation currentTransport;
    private int currentFieldIndex = 0;
    private final ServiceTransportation serviceTransportation = new ServiceTransportation();

    private final String[] fieldPrompts = {
            "Please enter the transportation type: ",
            "Please enter the provider name: ",
            "Please enter the departure point: ",
            "Please enter the arrival point: ",
            "Please enter the departure latitude (decimal): ",
            "Please enter the departure longitude (decimal): ",
            "Please enter the arrival latitude (decimal): ",
            "Please enter the arrival longitude (decimal): ",
            "Please enter the departure time (HH:MM:SS): ",
            "Please enter the duration in minutes (whole number): ",
            "Please enter the price (decimal): ",
            "Please enter the operating days (7 digits, 1=operating, 0=closed, e.g., 1111100): "
    };

    public String processInput(String input) {
        input = input.trim();

        switch (currentState) {
            case INITIAL:
                return handleInitialState(input);

            case COLLECTING_DATA:
                return handleCollectingDataState(input);

            case COMPLETED:
                currentState = State.INITIAL;
                return processInput(input);

            default:
                return "Any new transportation to be added? (yes/no)";
        }
    }

    private String handleInitialState(String input) {
        String lowerInput = input.toLowerCase();
        if (lowerInput.equals("help me add transportation") || lowerInput.equals("yes")) {
            currentTransport = new Transportation();
            currentTransport.setPhoto(""); // Always set photo to an empty string
            currentFieldIndex = 0;
            currentState = State.COLLECTING_DATA;
            return fieldPrompts[currentFieldIndex];
        } else if (lowerInput.equals("no")) {
            return "Have a good day! If you need to add transportation later, type 'help me add transportation'.";
        } else {
            return "Any new transportation to be added? (yes/no)";
        }
    }

    private String handleCollectingDataState(String input) {
        // Handle transport type first as it affects other fields
        if (currentFieldIndex == 0) {
            try {
                setTransportField(currentFieldIndex, input);

                // If transport type is NOT bus/train/ship, skip certain fields
                if (!isStandardTransportType(input)) {
                    setTransportField(1, input);
                    skipFieldsForNonStandardTransport();
                    return fieldPrompts[currentFieldIndex];
                }
            } catch (Exception e) {
                return "Error: " + e.getMessage() + "\nPlease enter a valid transport type\n" + fieldPrompts[currentFieldIndex];
            }
        }

        // For other fields
        if (!validateField(currentFieldIndex, input)) {
            return getFieldErrorMessage(currentFieldIndex) + "\n" + fieldPrompts[currentFieldIndex];
        }

        try {
            setTransportField(currentFieldIndex, input);
            currentFieldIndex = getNextFieldIndex(); // Get next field considering skips

            if (currentFieldIndex >= fieldPrompts.length) {
                currentTransport.setPhoto(""); // Reinforce empty photo before saving
                serviceTransportation.ajouter(currentTransport);
                currentState = State.COMPLETED;
                return "Thank you! I'll take care of that for you!\n\nAny new transportation to be added? (yes/no)";
            } else {
                return fieldPrompts[currentFieldIndex];
            }
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage() + "\nPlease try again.\n" + fieldPrompts[currentFieldIndex];
        }
    }

    private boolean isStandardTransportType(String type) {
        if (type == null) return false;
        String lowerType = type.toLowerCase();
        return lowerType.equals("bus") || lowerType.equals("train") || lowerType.equals("ship");
    }

    private void skipFieldsForNonStandardTransport() {
        currentTransport.setDeparturePoint("N/A");
        currentTransport.setArrivalPoint("N/A");
        currentTransport.setDepartureLat(0.0);
        currentTransport.setDepartureLng(0.0);
        currentTransport.setArrivalLat(0.0);
        currentTransport.setArrivalLng(0.0);
        currentTransport.setDepartureTime(LocalTime.of(0, 0));
        currentTransport.setDurationMinutes(0);
        currentTransport.setPrice(0.0);

        // After provider (index 1), jump to operating days (index 11)
        currentFieldIndex = 11;
    }


    private int getNextFieldIndex() {
        if (isStandardTransportType(currentTransport.getType())) {
            return currentFieldIndex + 1;
        }

        switch (currentFieldIndex) {
            case 0: return 1; // Ask provider next
            case 1:
                skipFieldsForNonStandardTransport(); // Skip after provider
                return currentFieldIndex; // Now at 11
            default: return currentFieldIndex + 1;
        }
    }


    private void setTransportField(int index, String value) throws Exception {
        switch (index) {
            case 0: currentTransport.setType(value); break;
            case 1: currentTransport.setProviderName(value); break;
            case 2: currentTransport.setDeparturePoint(value); break;
            case 3: currentTransport.setArrivalPoint(value); break;
            case 4: currentTransport.setDepartureLat(Double.parseDouble(value)); break;
            case 5: currentTransport.setDepartureLng(Double.parseDouble(value)); break;
            case 6: currentTransport.setArrivalLat(Double.parseDouble(value)); break;
            case 7: currentTransport.setArrivalLng(Double.parseDouble(value)); break;
            case 8: currentTransport.setDepartureTime(LocalTime.parse(value)); break;
            case 9: currentTransport.setDurationMinutes(Integer.parseInt(value)); break;
            case 10: currentTransport.setPrice(Double.parseDouble(value)); break;
            case 11:
                if (!value.matches("[01]{7}")) {
                    throw new IllegalArgumentException("Operating days must be 7 digits (0 or 1)");
                }
                currentTransport.setOperatingDays(value);
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid field index: " + index);
        }
    }

    private boolean validateField(int index, String value) {
        if (!isStandardTransportType(currentTransport.getType())) {
            if (index >= 2 && index <= 9) return true;
        }

        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        try {
            switch (index) {
                case 4: case 5: case 6: case 7:
                    Double.parseDouble(value); return true;
                case 8:
                    LocalTime.parse(value); return true;
                case 9:
                    Integer.parseInt(value); return true;
                case 10:
                    Double.parseDouble(value); return true;
                case 11:
                    return value.matches("[01]{7}");
                default:
                    return true;
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            return false;
        }
    }

    private String getFieldErrorMessage(int index) {
        switch (index) {
            case 4: case 5: case 6: case 7:
                return "Error: Please enter a valid decimal number";
            case 8:
                return "Error: Please enter time in HH:MM:SS format (e.g., 08:30:00)";
            case 9:
                return "Error: Please enter a whole number for duration";
            case 10:
                return "Error: Please enter a valid price (e.g., 12.50)";
            case 11:
                return "Error: Operating days should be exactly 7 digits (1=operating, 0=closed, e.g., 1111100)";
            default:
                return "Error: This field cannot be empty";
        }
    }
}
