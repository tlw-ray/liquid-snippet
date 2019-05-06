package com.winning.ptc.liquid.snippet.a07_system;

import com.winning.ptc.liquid.snippet.a07_system.model.DiffResult;
import com.winning.ptc.liquid.snippet.a07_system.model.SnapshotInfo;

import java.util.Date;
import java.util.UUID;

public interface SnapshotController {
    SnapshotInfo createSnapshot(SnapshotInfo snapshotInfo);
    void deleteSnapshot(UUID snapshotInfoRef);
    SnapshotInfo updateSnapshot(SnapshotInfo snapshotInfo);
    SnapshotInfo[] listSnapshot(UUID connectionRef, Date from, Date to);

    DiffResult diff(UUID snapshot1, UUID snapshot2);
}
