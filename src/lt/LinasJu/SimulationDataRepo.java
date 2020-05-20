package lt.LinasJu;

import lt.LinasJu.Entities.SimulationOutputData.TimestepData;
import lt.LinasJu.Entities.SimulationOutputData.TimestepVehicle;
import lt.LinasJu.Entities.SimulationOutputData.Vehicle;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SimulationDataRepo extends DefaultHandler {

    private List<TimestepData> outputDataToCompareList = null;
    private TimestepData anOutputData = null;
    private StringBuilder data = null;
    private List<TimestepVehicle> timestepVehicles = new ArrayList<>();
    private TimestepVehicle timestepVehicle = null;

    // getter method for employee list
    public List<TimestepData> getOutputDataToCompareList() {
        return outputDataToCompareList;
    }

    boolean hasVehicle = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equalsIgnoreCase("timestep")) {
            // create a new Itemt and put it in Map
            String time = attributes.getValue("time");
            // initialize Timestamp object and set time attribute
            anOutputData = new TimestepData();
            anOutputData.setTime(time != null ? Float.parseFloat(time) : null);
            timestepVehicles = new ArrayList<>();
            // initialize list
            if (outputDataToCompareList == null)
                outputDataToCompareList = new ArrayList<>();
        } else if (qName.equalsIgnoreCase("vehicle")) {
            // set boolean values for fields, will be used in setting Timestep variables
            hasVehicle = true;
            timestepVehicle = new TimestepVehicle();
            Field[] vehicleFields = TimestepVehicle.class.getDeclaredFields();
            Arrays.stream(vehicleFields).forEach(field -> {
                field.setAccessible(true);

                if (field.getType() == Float.class) {
                    Object attribute = attributes.getValue(field.getName());
                    Float attributeFloat = attribute != null ? Float.parseFloat(String.valueOf(attribute)) : null;
                    switch (field.getName()) {
                        case "speed":
                            timestepVehicle.setSpeed(attributeFloat);
                            break;
                        case "CO2":
                            timestepVehicle.setCO2(attributeFloat);
                            break;
                        case "fuel":
                            timestepVehicle.setFuel(attributeFloat);
                            break;
                        case "noise":
                            timestepVehicle.setNoise(attributeFloat);
                            break;
                        case "waiting":
                            timestepVehicle.setWaiting(attributeFloat);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + field.getName());
                    }
                    return;
                }
                if (field.getType() == Integer.class) {
                    Object attribute = attributes.getValue(field.getName());
                    Integer attributeInteger = attribute != null ? Integer.parseInt(String.valueOf(attribute)) : null;
                    if ("id".equals(field.getName())) {
                        timestepVehicle.setId(attributeInteger);
                    } else {
                        throw new IllegalStateException("Unexpected value: " + field.getName());
                    }
                    return;
                }
                if (field.getType() == String.class) {
                    Object attribute = attributes.getValue(field.getName());
                    String attributeString = attribute != null ? String.valueOf(attribute) : null;
                    if ("lane".equals(field.getName())) {
                        timestepVehicle.setLane(attributeString);
                    } else {
                        throw new IllegalStateException("Unexpected value: " + field.getName());
                    }
                }
            });
            timestepVehicles.add(timestepVehicle);
        }
        // create the data container
        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (hasVehicle) {
            anOutputData.setTimestepVehicles(timestepVehicles);
            hasVehicle = false;
        }

         if (qName.equalsIgnoreCase("timestep")) {
            // add timestep object to list
            outputDataToCompareList.add(anOutputData);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        data.append(new String(ch, start, length));
    }

    public List<TimestepData> getTimestepDataFromXmlFiles(String workingDirectory, String fileName, List<SumoOutputDataFilesEnum> simulationOutputFileTypes) {
        String fileNameBase = workingDirectory + fileName;
        List<String> fileNames = simulationOutputFileTypes.stream().map(outputFileEnum -> fileNameBase + outputFileEnum.getFileEndWithSuffixXml()).collect(Collectors.toList());

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        List<TimestepData> timestepDataList = new ArrayList<>();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            SimulationDataRepo handler = new SimulationDataRepo();
            saxParser.parse(new File(fileNames.get(0)), handler); //todo kol kas skaitoma is vieno failo (realiai kol kas jo ir uztenka)
            //Get SimulationOutputData list
            timestepDataList = handler.getOutputDataToCompareList();
            //print SimulationOutputData information

//output for debugging            for (TimestepData timestepData : timestepDataList)
//                System.out.println(timestepData.getTime() + " vehicle no." + timestepData.getVehicles().size());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return timestepDataList;
    }


    public List<Vehicle> getVehiclesSimulationOutput(String workingDirectory, String fileName, List<SumoOutputDataFilesEnum> simulationOutputFileTypes) {
        List<TimestepData> rawSimulationOutputData = getTimestepDataFromXmlFiles(workingDirectory, fileName, simulationOutputFileTypes);

        Map<Integer, Vehicle> vehicleList = new HashMap<>();
        for (TimestepData timestepData : rawSimulationOutputData) {
            for (TimestepVehicle timestepVehicle : timestepData.getTimestepVehicles()) {
                if (vehicleList.get(timestepVehicle.getId()) == null) {
                    vehicleList.put(timestepVehicle.getId(), new Vehicle(timestepVehicle.getId()));
                }
                Vehicle vehicle = vehicleList.get(timestepVehicle.getId());
                vehicle.getSpeeds().add(timestepVehicle.getSpeed());
                vehicle.getCO2s().add(timestepVehicle.getCO2());
                vehicle.getFuels().add(timestepVehicle.getFuel());
                vehicle.getNoises().add(timestepVehicle.getNoise());
                vehicle.getWaitings().add(timestepVehicle.getWaiting());
                vehicle.getLanes().add(timestepVehicle.getLane());
            }
        }
        return new ArrayList<>(vehicleList.values());
    }
}
