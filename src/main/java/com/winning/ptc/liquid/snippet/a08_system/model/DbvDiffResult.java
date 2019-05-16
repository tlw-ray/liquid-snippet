package com.winning.ptc.liquid.snippet.a08_system.model;

import lombok.Data;

@Data
public class DbvDiffResult {
    String report;
    String patchQuery;

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getPatchQuery() {
        return patchQuery;
    }

    public void setPatchQuery(String patchQuery) {
        this.patchQuery = patchQuery;
    }
}
