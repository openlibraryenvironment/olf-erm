databaseChangeLog = {	
	changeSet(author: "claudia (manual)", id: "2019-11-04-00001") {
		createTable(tableName: "order_line") {
			column(name: "pol_id", type: "VARCHAR(36)") {
				constraints(nullable: "false")
			}
			column(name: "pol_version", type: "BIGINT") {
				constraints(nullable: "false")
			}
			column(name: "pol_orders_fk", type: "VARCHAR(50)") {
				constraints(nullable: "false")
			}
			column(name: "pol_owner_fk", type: "VARCHAR(36)") {
				constraints(nullable: "false")
			}
		}
	  }
	  
	  changeSet(author: "claudia (manual)", id: "2019-11-04-00002") {
		  addPrimaryKey(columnNames: "pol_id", constraintName: "order_linePK", tableName: "order_line")
	  }
	  
	  changeSet(author: "claudia (manual)", id: "2019-11-04-00003") {
		  addForeignKeyConstraint(baseColumnNames: "pol_owner_fk",
									baseTableName: "order_line",
								  constraintName: "pol_to_ent_fk",
								  deferrable: "false", initiallyDeferred: "false",
								  referencedColumnNames: "ent_id", referencedTableName: "entitlement")
	  }
	  
	  changeSet(author: "claudia (manual)", id: "2019-11-06-0001") {
		  grailsChange {
			change {
			  // Add order_line entry for each existing entitlement.
			  sql.execute("""
            INSERT INTO ${database.defaultSchemaName}.order_line (pol_id, pol_owner_fk, pol_version, pol_orders_fk )
                SELECT
                  md5(random()::text || clock_timestamp()::text)::uuid as id,
                  ent_id as oid,
                  1 as v,
                  ent_po_line_id as poid
                FROM 
                  ${database.defaultSchemaName}.entitlement;
        """.toString())
			}
		  }
		}
	  
	  changeSet(author: "claudia (manual)", id: "2019-11-06-0002") {
		  dropColumn(columnName: "ent_po_line_id", tableName: "entitlement")
	  }
}
