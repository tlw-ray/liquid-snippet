package com.winning.ptc.liquid.snippet.a07_system.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class SnapshotInfo {
    UUID connectionUUID;
    Date date;
    String description;
    UUID snapshotUUID;
}
