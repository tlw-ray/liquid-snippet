package com.winning.ptc.liquid.snippet.a08_system.model;

import lombok.Data;

import java.util.UUID;

@Data
public class ConnectionInfo {
    UUID uuid;
    DBType type;
    String driver;
    String url;
    String userName;
    String password;
}
