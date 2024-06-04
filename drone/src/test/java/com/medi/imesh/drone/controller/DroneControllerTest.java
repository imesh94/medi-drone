package com.medi.imesh.drone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medi.imesh.drone.common.ApplicationConstants;
import com.medi.imesh.drone.dto.DroneDTO;
import com.medi.imesh.drone.service.DroneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DroneController.class)
public class DroneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DroneService droneService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void registerDrone_WhenValidInput_Returns200() throws Exception {

        DroneDTO droneDTO = new DroneDTO();
        droneDTO.setSerialNumber("DR001");
        droneDTO.setModel("Heavyweight");
        droneDTO.setState(ApplicationConstants.DroneState.IDLE);
        droneDTO.setBatteryCapacity(100);
        droneDTO.setWeightLimit(400);

        given(droneService.registerDrone(any(DroneDTO.class))).willReturn(droneDTO);

        mockMvc.perform(post("/drones/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(droneDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(droneDTO)));
    }

    @Test
    public void getDroneById_WhenExists_Returns200() throws Exception {

        String droneSerialNo = "DR001";
        DroneDTO droneDTO = new DroneDTO();
        droneDTO.setSerialNumber("DR001");

        given(droneService.findDroneBySerialNumber(droneSerialNo)).willReturn(Optional.of(droneDTO));

        mockMvc.perform(get("/drones/" + droneSerialNo)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(droneDTO)));
    }

    @Test
    public void getDroneBySerial_WhenDoesNotExist_Returns404() throws Exception {

        String droneSerialNo = "DR001";
        given(droneService.findDroneBySerialNumber(droneSerialNo)).willReturn(Optional.empty());

        mockMvc.perform(get("/drones/" + droneSerialNo)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteDrone_WhenExists_Returns204() throws Exception {

        String serialNumber = "DR001";
        given(droneService.deleteDrone(serialNumber)).willReturn(true);

        mockMvc.perform(delete("/drones/{serialNumber}", serialNumber))
                .andExpect(status().isNoContent());

        verify(droneService).deleteDrone(serialNumber);
    }

}