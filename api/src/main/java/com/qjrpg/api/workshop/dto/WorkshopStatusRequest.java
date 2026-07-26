package com.qjrpg.api.workshop.dto;
import com.qjrpg.api.workshop.StatusWorkshop;
import jakarta.validation.constraints.NotNull;
public record WorkshopStatusRequest(@NotNull StatusWorkshop status) {}
