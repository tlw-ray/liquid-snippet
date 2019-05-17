package com.winning.ptc.liquid.snippet.a02_diff;

import liquibase.command.core.DiffCommand;
import liquibase.diff.DiffResult;
import liquibase.exception.DatabaseException;
import liquibase.snapshot.InvalidExampleException;

//要使用自定义的MyDiffToReport需要将createDiffResult从protected开放为public来获得DiffResult对象
public class MyDiffCommand extends DiffCommand {
    public DiffResult createDiffResult() throws DatabaseException, InvalidExampleException {
        return super.createDiffResult();
    }
}
