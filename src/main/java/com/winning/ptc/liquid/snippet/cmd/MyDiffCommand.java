package com.winning.ptc.liquid.snippet.cmd;

import liquibase.command.core.DiffCommand;
import liquibase.diff.DiffResult;
import liquibase.exception.DatabaseException;
import liquibase.snapshot.InvalidExampleException;

//使外部可以直接使用DiffResult
public class MyDiffCommand extends DiffCommand {
    public DiffResult createDiffResult() throws DatabaseException, InvalidExampleException {
        return super.createDiffResult();
    }
}
