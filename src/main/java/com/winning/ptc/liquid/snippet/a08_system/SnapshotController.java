package com.winning.ptc.liquid.snippet.a08_system;

import com.winning.ptc.liquid.snippet.a08_system.model.DbvDiffResult;
import com.winning.ptc.liquid.snippet.a08_system.model.SnapshotInfo;

import java.util.Date;
import java.util.UUID;

public interface SnapshotController {
    SnapshotInfo createSnapshot(SnapshotInfo snapshotInfo);
    void deleteSnapshot(UUID snapshotInfoRef);
    SnapshotInfo updateSnapshot(SnapshotInfo snapshotInfo);
    SnapshotInfo[] listSnapshot(UUID connectionRef, Date from, Date to);

    DbvDiffResult diff(UUID snapshot1, UUID snapshot2);
}
