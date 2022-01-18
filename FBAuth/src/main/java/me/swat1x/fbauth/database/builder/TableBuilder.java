package me.swat1x.fbauth.database.builder;

import me.swat1x.fbauth.database.Database;

import java.util.ArrayList;
import java.util.List;

public class TableBuilder {

    private final String tableName;

    public TableBuilder(String tableName){
        this.tableName = tableName;
    }

    private final List<String> args = new ArrayList<>();

    public TableBuilder appendColumn(String name, String type){
        args.add("`"+name+"` "+type);
        return this;
    }

    public void build(Database database){
        database.sync().update("CREATE TABLE IF NOT EXISTS `"+tableName+"` ("+String.join(", ", args)+") ENGINE=InnoBD DEFAULT CHARACTER SET=utf8mb4 COLLATE utf8mb4_bin");
    }

}
