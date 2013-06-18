package com.worldcretornica.plotme_core;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bukkit.World;

import com.worldcretornica.plotme_core.api.v0_14b.IPlotMe_GeneratorManager;

public class SqlManager {

	private static Connection conn = null;
	
	public final static String sqlitedb = "plots.db";
	
	//todo add update to table for customprice, forsale
	
    private final static String PLOT_TABLE = "CREATE TABLE `plotmePlots` ("
		    + "`idX` INTEGER,"
		    + "`idZ` INTEGER,"
		    + "`owner` varchar(32) NOT NULL,"
	        + "`world` varchar(32) NOT NULL DEFAULT '0',"
	        + "`topX` INTEGER NOT NULL DEFAULT '0',"
	        + "`bottomX` INTEGER NOT NULL DEFAULT '0',"
	        + "`topZ` INTEGER NOT NULL DEFAULT '0',"
	        + "`bottomZ` INTEGER NOT NULL DEFAULT '0',"
	        + "`biome` varchar(32) NOT NULL DEFAULT '0',"
	        + "`expireddate` DATETIME NULL,"
	        + "`finished` boolean NOT NULL DEFAULT '0',"
	        + "`customprice` double NOT NULL DEFAULT '0',"
	        + "`forsale` boolean NOT NULL DEFAULT '0',"
	        + "`finisheddate` varchar(16) NULL,"
	        + "`protected` boolean NOT NULL DEFAULT '0',"
	        + "`auctionned` boolean NOT NULL DEFAULT '0',"
	        + "`auctionneddate` varchar(16) NULL,"
	        + "`currentbid` double NOT NULL DEFAULT '0',"
	        + "`currentbidder` varchar(32) NULL,"
	        + "PRIMARY KEY (idX, idZ, world) "
	        + ");";
    
    private final static String COMMENT_TABLE = "CREATE TABLE `plotmeComments` ("
	    	+ "`idX` INTEGER,"
		    + "`idZ` INTEGER,"
	    	+ "`world` varchar(32) NOT NULL,"
		    + "`commentid` INTEGER,"
		    + "`player` varchar(32) NOT NULL,"
		    + "`comment` text,"
	        + "PRIMARY KEY (idX, idZ, world, commentid) "
	    	+ ");";
    
    private final static String ALLOWED_TABLE = "CREATE TABLE `plotmeAllowed` ("
    		+ "`idX` INTEGER,"
    	    + "`idZ` INTEGER,"
    	    + "`world` varchar(32) NOT NULL,"
    	    + "`player` varchar(32) NOT NULL,"
	        + "PRIMARY KEY (idX, idZ, world, player) "
    	    + ");";
    
    private final static String DENIED_TABLE = "CREATE TABLE `plotmeDenied` ("
    		+ "`idX` INTEGER,"
    	    + "`idZ` INTEGER,"
    	    + "`world` varchar(32) NOT NULL,"
    	    + "`player` varchar(32) NOT NULL,"
	        + "PRIMARY KEY (idX, idZ, world, player) "
    	    + ");";
		
    public static Connection initialize() 
    {
        try 
        {
        	if(PlotMe_Core.usemySQL) 
        	{
        		Class.forName("com.mysql.jdbc.Driver");
        		conn = DriverManager.getConnection(PlotMe_Core.mySQLconn, PlotMe_Core.mySQLuname, PlotMe_Core.mySQLpass);
        		conn.setAutoCommit(false);
        	} 
        	else 
        	{
        		Class.forName("org.sqlite.JDBC");
        		conn = DriverManager.getConnection("jdbc:sqlite:" + PlotMe_Core.configpath + "/plots.db");
        		conn.setAutoCommit(false);
        	}
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("SQL exception on initialize :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        catch (ClassNotFoundException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("You need the SQLite/MySQL library. :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        }
        
        createTable();
        
        return conn;
    }
    
    public static String getSchema()
    {
    	String conn = PlotMe_Core.mySQLconn;
    	
    	if(conn.lastIndexOf("/") > 0)
    		return conn.substring(conn.lastIndexOf("/") + 1);
    	else
    		return "";
    }
    
    public static void UpdateTables()
    {
        Statement statement = null;
        ResultSet set = null;
		
        try 
        {
            Connection conn = getConnection();

            statement = conn.createStatement();
            
            String schema = getSchema();
        	            
            if(PlotMe_Core.usemySQL)
            {
            	/*** START Version 0.8 changes ***/
            	//CustomPrice
            	set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " +
            			"TABLE_NAME='plotmePlots' AND column_name='customprice'");
            	if(!set.next())
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD customprice double NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	
            	//ForSale
            	set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " +
            			"TABLE_NAME='plotmePlots' AND column_name='forsale'");
            	if(!set.next())
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD forsale boolean NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	
            	//finisheddate
            	set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " +
            			"TABLE_NAME='plotmePlots' AND column_name='finisheddate'");
            	if(!set.next())
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD finisheddate varchar(16) NULL;");
            		conn.commit();
            	}
            	set.close();
            	
            	//Protected
            	set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " +
            			"TABLE_NAME='plotmePlots' AND column_name='protected'");
            	if(!set.next())
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD protected boolean NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	
            	//Auctionned
            	set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " +
            			"TABLE_NAME='plotmePlots' AND column_name='auctionned'");
            	if(!set.next())
            	{
            		statement.executeUpdate("ALTER TABLE plotmePlots ADD auctionned boolean NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	
            	//Auctionenddate
            	set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " +
            			"TABLE_NAME='plotmePlots' AND (column_name='auctionenddate' OR column_name='auctionneddate')");
            	if(!set.next())
            	{
            		statement.executeUpdate("ALTER TABLE plotmePlots ADD auctionenddate varchar(16) NULL;");
            		conn.commit();
            	}
            	set.close();
            	
            	//Currentbidder
            	set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " +
            			"TABLE_NAME='plotmePlots' AND column_name='currentbidder'");
            	if(!set.next())
            	{
            		statement.executeUpdate("ALTER TABLE plotmePlots ADD currentbidder varchar(32) NULL;");
            		conn.commit();
            	}
            	set.close();
            	
            	//Currentbid
            	set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " +
            			"TABLE_NAME='plotmePlots' AND column_name='currentbid'");
            	if(!set.next())
            	{
            		statement.executeUpdate("ALTER TABLE plotmePlots ADD currentbid double NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	
            	/*** END Version 0.8 changes ***/
            	
            	/*** START Version 0.14 changes ***/
            	//Rename auctionneddate correctly
            	set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " +
            			"TABLE_NAME='plotmePlots' AND column_name='auctionenddate'");
            	if(set.next())
            	{
            		statement.execute("ALTER TABLE plotmePlots CHANGE auctionenddate auctionneddate varchar(16) NULL;");
            		conn.commit();
            	}
            	set.close();
            	/*** END Version 0.14 changes ***/
            }
            else
            {
            	String column;
            	boolean found = false;
            	
            	/*** START Version 0.8 changes ***/
            	//CustomPrice
            	set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
            	while(set.next() && !found)
            	{
            		column = set.getString(2);
            		if(column.equalsIgnoreCase("customprice"))
            			found = true;
            	}

            	if(!found)
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD customprice double NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	found = false;
            	
            	//ForSale
            	set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
            	            	
            	while(set.next() && !found)
            	{
            		column = set.getString(2);
            		if(column.equalsIgnoreCase("forsale"))
            			found = true;
            	}
            	
            	if(!found)
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD forsale boolean NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	found = false;
            	
            	//FinishedDate
            	set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
            	            	
            	while(set.next() && !found)
            	{
            		column = set.getString(2);
            		if(column.equalsIgnoreCase("finisheddate"))
            			found = true;
            	}
            	
            	if(!found)
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD finisheddate varchar(16) NULL;");
            		conn.commit();
            	}
            	set.close();
            	found = false;
            	
            	//Protected
            	set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
            	            	
            	while(set.next() && !found)
            	{
            		column = set.getString(2);
            		if(column.equalsIgnoreCase("protected"))
            			found = true;
            	}
            	
            	if(!found)
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD protected boolean NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	found = false;
            	
            	//Auctionned
            	set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
            	            	
            	while(set.next() && !found)
            	{
            		column = set.getString(2);
            		if(column.equalsIgnoreCase("auctionned"))
            			found = true;
            	}
            	
            	if(!found)
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD auctionned boolean NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	found = false;
            	
            	//Auctionenddate
            	set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
            	            	
            	while(set.next() && !found)
            	{
            		column = set.getString(2);
            		if(column.equalsIgnoreCase("auctionenddate") || column.equalsIgnoreCase("auctionneddate"))
            			found = true;
            	}
            	
            	if(!found)
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD auctionenddate varchar(16) NULL;");
            		conn.commit();
            	}
            	set.close();
            	found = false;
            	
            	//Currentbidder
            	set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
            	
            	while(set.next() && !found)
            	{
            		column = set.getString(2);
            		if(column.equalsIgnoreCase("currentbidder"))
            			found = true;
            	}
            	            	
            	if(!found)
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD currentbidder varchar(32) NULL;");
            		conn.commit();
            	}
            	set.close();
            	found = false;
            	
            	//Currentbid
            	set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
            	            	
            	while(set.next() && !found)
            	{
            		column = set.getString(2);
            		if(column.equalsIgnoreCase("currentbid"))
            			found = true;
            	}
            	
            	if(!found)
            	{
            		statement.execute("ALTER TABLE plotmePlots ADD currentbid double NOT NULL DEFAULT '0';");
            		conn.commit();
            	}
            	set.close();
            	found = false;
            	/*** END Version 0.8 changes ***/
            	
            	/*** START Version 0.14 changes ***/          	
            	//Rename auctionneddate correctly
            	set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
            	
            	while(set.next() && !found)
            	{
            		column = set.getString(2);
            		if(column.equalsIgnoreCase("auctionenddate"))
            			found = true;
            	}
            	
            	if(found)
            	{
            		//statement.execute("ALTER TABLE plotmePlots CHANGE auctionenddate auctionneddate varchar(16) NULL;"); <- doesn't work
            		
            		ResultSet plotset = null;
            		Statement plotstatement = conn.createStatement();
            		
            		plotset = plotstatement.executeQuery("SELECT SQL FROM SQLITE_MASTER WHERE NAME = 'plotmePlots';");
            		
            		if(plotset.next())
            		{
            			String createstring = plotset.getString(1);
            			createstring = createstring.replace("auctionenddate", "auctionneddate").replace("'", "''");
            			
            			Statement changestatement = conn.createStatement();
            			changestatement.execute("PRAGMA writable_schema = 1");
            			changestatement.execute("UPDATE SQLITE_MASTER SET SQL = '" + createstring + "' WHERE NAME = 'plotmePlots'");
            			changestatement.execute("PRAGMA writable_schema = 0");
            			conn.commit();
            			changestatement.close();
            			changestatement = null;
            		}
            		plotstatement.close();
            		plotstatement = null;
                    plotset.close();
                    plotset = null;
            		
            		conn.commit();
            	}
            	set.close();
            	set = null;
            	found = false;
            	/*** END Version 0.14 changes ***/
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Update table exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (statement != null)
                	statement.close();
                if (set != null)
                	set.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Update table exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }

	public static Connection getConnection()
	{
		if(conn == null) conn = initialize();
		if(PlotMe_Core.usemySQL) 
		{
			try 
			{
				if(!conn.isValid(10)) conn = initialize();
			} 
			catch (SQLException ex) 
			{
				PlotMe_Core.self.getLogger().severe("Failed to check SQL status :");
				PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
			}
		}
		return conn;
	}

    public static void closeConnection()
    {
		if(conn != null) 
		{
			try {
				if(PlotMe_Core.usemySQL)
				{
					if(conn.isValid(10)) 
					{
						conn.close();
					}
					conn = null;
				} 
				else 
				{
					conn.close();
					conn = null;
				}
			} 
			catch (SQLException ex) 
			{
				PlotMe_Core.self.getLogger().severe("Error on Connection close :");
				PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
			}
		}
    }
    
    private static boolean tableExists(String name)
    {
        ResultSet rs = null;
        try 
        {
            Connection conn = getConnection();

            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, name, null);
            if (!rs.next())
                return false;
            return true;
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Table Check Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            return false;
        } 
        finally 
        {
            try 
            {
                if (rs != null)
                    rs.close();
            } catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Table Check SQL Exception (on closing) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    private static void createTable()
    {
    	Statement st = null;
    	try 
    	{
    		//PlotMe.logger.info(PlotMe.PREFIX + " Creating Database...");
    		Connection conn = getConnection();
    		st = conn.createStatement();
    		
    		if(!tableExists("plotmePlots"))
    		{
	    		st.executeUpdate(PLOT_TABLE);
	    		conn.commit();
    		}
    		
    		if(!tableExists("plotmeComments"))
    		{
	    		st.executeUpdate(COMMENT_TABLE);
	    		conn.commit();
    		}
    		
    		if(!tableExists("plotmeAllowed"))
    		{
	    		st.executeUpdate(ALLOWED_TABLE);
	    		conn.commit();
    		}
    		
    		if(!tableExists("plotmeDenied"))
    		{
	    		st.executeUpdate(DENIED_TABLE);
	    		conn.commit();
    		}
    		
    		UpdateTables();
    		
    		if(PlotMe_Core.usemySQL)
    		{ 
    			PlotMe_Core.self.getLogger().info("Modifying database for MySQL support");
    			
    			File sqlitefile = new File(PlotMe_Core.configpath, sqlitedb);
    			if (!sqlitefile.exists()) {
    				//PlotMe_Core.self.getLogger().info("Could not find old " + sqlitedb);
    				return;
    			} 
    			else 
    			{
    				PlotMe_Core.self.getLogger().info("Trying to import plots from plots.db");
	        		Class.forName("org.sqlite.JDBC");
	        		Connection sqliteconn = DriverManager.getConnection("jdbc:sqlite:" + PlotMe_Core.configpath + "\\" + sqlitedb);

	        		sqliteconn.setAutoCommit(false);
	        		Statement slstatement = sqliteconn.createStatement();
	        		ResultSet setPlots = slstatement.executeQuery("SELECT * FROM plotmePlots");
	        		Statement slAllowed = sqliteconn.createStatement();
	        		ResultSet setAllowed = null;
	        		Statement slDenied = sqliteconn.createStatement();
	        		ResultSet setDenied = null;
	        		Statement slComments = sqliteconn.createStatement();
	        		ResultSet setComments = null;
	        		
	        		int size = 0;
	        		while (setPlots.next()) 
	        		{	        			
	        			int idX = setPlots.getInt("idX");
	        			int idZ = setPlots.getInt("idZ");
	        			String owner = setPlots.getString("owner");
	        			String world = setPlots.getString("world").toLowerCase();
	        			int topX = setPlots.getInt("topX");
	        			int bottomX = setPlots.getInt("bottomX");
	        			int topZ = setPlots.getInt("topZ");
	        			int bottomZ = setPlots.getInt("bottomZ");
	        			String biome = setPlots.getString("biome");
	        			java.sql.Date expireddate = null;
	        			try
	        			{
	        				expireddate = setPlots.getDate("expireddate");
	        			}catch(Exception e){}
	        			boolean finished = setPlots.getBoolean("finished");
	        			HashSet<String> allowed = new HashSet<String>();
	        			HashSet<String> denied = new HashSet<String>();
	        			List<String[]> comments = new ArrayList<String[]>();
	        			double customprice = setPlots.getDouble("customprice");
	        			boolean forsale = setPlots.getBoolean("forsale");
	        			String finisheddate = setPlots.getString("finisheddate");
	        			boolean protect = setPlots.getBoolean("protected");
	        			boolean auctionned = setPlots.getBoolean("auctionned");
	        			String currentbidder = setPlots.getString("currentbidder");
	        			double currentbid = setPlots.getDouble("currentbid");
	        			String auctionneddate;
	        			try
	        			{
	        				auctionneddate = setPlots.getString("auctionneddate");
	        			}catch(Exception e)
	        			{
	        				auctionneddate = setPlots.getString("auctionenddate");
	        			}
	        			
	        			setAllowed = slAllowed.executeQuery("SELECT * FROM plotmeAllowed WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");

	        			while (setAllowed.next()) 
	        			{
	        				allowed.add(setAllowed.getString("player"));
	        			}
	        			
	        			if (setAllowed != null) 
	        			{
	        				setAllowed.close();
	        			}
	        			
	        			setDenied = slDenied.executeQuery("SELECT * FROM plotmeDenied WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");

	        			while (setDenied.next()) 
	        			{
	        				denied.add(setDenied.getString("player"));
	        			}
	        			
	        			if (setDenied != null) 
	        			{
	        				setDenied.close();
	        			}
	        			
	        			setComments = slComments.executeQuery("SELECT * FROM plotmeComments WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");

	        			while (setComments.next()) 
	        			{
	        				String[] comment = new String[2];
	        				comment[0] = setComments.getString("player");
	        				comment[1] = setComments.getString("comment");
	        				comments.add(comment);
	        			}
	        			
	        			Plot plot = new Plot(owner, world, biome, expireddate, finished, allowed,
	        					 comments, "" + idX + ";" + idZ, customprice, forsale, finisheddate, 
	        					 protect, currentbidder, currentbid, auctionned, denied, auctionneddate);
	        			addPlot(plot, idX, idZ, topX, bottomX, topZ, bottomZ);

	        			size++;
	        		}
	        		
	        		PlotMe_Core.self.getLogger().info("Imported " + size + " plots from " + sqlitedb);
	        		if (slstatement != null)
        				slstatement.close();
	        		if (slAllowed != null)
	        			slAllowed.close();
	        		if (slComments != null)
	        			slComments.close();
	        		if (slDenied != null)
	        			slDenied.close();
        			if (setPlots != null)
        				setPlots.close();
        			if (setComments != null)
                    	setComments.close();
                    if (setDenied != null)
                        setDenied.close();
                    if (setAllowed != null)
                    	setAllowed.close();
    				if (sqliteconn != null)
        				sqliteconn.close();
    				
	        		PlotMe_Core.self.getLogger().info("Renaming " + sqlitedb + " to " + sqlitedb + ".old");
	        		if (!sqlitefile.renameTo(new File(PlotMe_Core.configpath, sqlitedb + ".old"))) 
	        		{
	        			PlotMe_Core.self.getLogger().severe("Failed to rename " + sqlitedb + "! Please rename this manually!");
	    			}
    			}
    		}
    	} 
    	catch (SQLException ex) 
    	{
    		PlotMe_Core.self.getLogger().severe("Create Table Exception :");
    		PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
    	} 
    	catch (ClassNotFoundException ex) 
    	{
    		PlotMe_Core.self.getLogger().severe("You need the SQLite library :");
    		PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
    	} 
    	finally 
    	{
    		try {
    			if (st != null) 
    			{
    				st.close();
    			}
    		} 
    		catch (SQLException ex) 
    		{
    			PlotMe_Core.self.getLogger().severe("Could not create the table (on close) :");
    			PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
    		}
    	}
    }
    
    public static void addPlot(Plot plot, int idX, int idZ, World w)
    {
    	IPlotMe_GeneratorManager gm = PlotMeCoreManager.getGenMan(w);
    	addPlot(plot, idX, idZ, gm.topX(plot.id, w), gm.bottomX(plot.id, w), gm.topZ(plot.id, w), gm.bottomZ(plot.id, w));
    }
        
    public static void addPlot(Plot plot, int idX, int idZ, int topX, int bottomX, int topZ, int bottomZ)
    {
        PreparedStatement ps = null;
        Connection conn;
        
        //Plots
        try 
        {
            conn = getConnection();

            ps = conn.prepareStatement("INSERT INTO plotmePlots (idX, idZ, owner, world, topX, bottomX, topZ, bottomZ, biome, " +
            		"expireddate, finished, customprice, forsale, finisheddate, protected, auctionned, auctionneddate, currentbid, currentbidder) " +
					   "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, plot.owner);
            ps.setString(4, plot.world.toLowerCase());
            ps.setInt(5, topX);
            ps.setInt(6, bottomX);
            ps.setInt(7, topZ);
            ps.setInt(8, bottomZ);
            ps.setString(9, plot.biome.name());
            ps.setDate(10, plot.expireddate);
            ps.setBoolean(11, plot.finished);
            ps.setDouble(12, plot.customprice);
            ps.setBoolean(13, plot.forsale);
            ps.setString(14, plot.finisheddate);
            ps.setBoolean(15, plot.protect);
            ps.setBoolean(16, plot.auctionned);
            ps.setString(17, "");
            ps.setDouble(18, plot.currentbid);
            ps.setString(19, plot.currentbidder);
            
            ps.executeUpdate();
            conn.commit();
                        
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Insert Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Insert Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void updatePlot(int idX, int idZ, String world, String field, Object value)
    {
        PreparedStatement ps = null;
        Connection conn;
        
        //Plots
        try 
        {
            conn = getConnection();

            ps = conn.prepareStatement("UPDATE plotmePlots SET " + field + " = ? " +
            						   "WHERE idX = ? AND idZ = ? AND world = ?");
            
            ps.setObject(1, value);
            ps.setInt(2, idX);
            ps.setInt(3, idZ);
            ps.setString(4, world.toLowerCase());
            
            ps.executeUpdate();
            conn.commit();
                        
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Insert Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Insert Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void addPlotAllowed(String player, int idX, int idZ, String world)
    {
    	PreparedStatement ps = null;
        Connection conn;
        
    	//Allowed
        try 
        {
            conn = getConnection();
            
            ps = conn.prepareStatement("INSERT INTO plotmeAllowed (idX, idZ, player, world) " +
					   "VALUES (?,?,?,?)");
            
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world.toLowerCase());
            
            ps.executeUpdate();
            conn.commit();
            
        } catch (SQLException ex) {
        	PlotMe_Core.self.getLogger().severe("Insert Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Insert Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void addPlotDenied(String player, int idX, int idZ, String world)
    {
    	PreparedStatement ps = null;
        Connection conn;
        
    	//Denied
        try 
        {
            conn = getConnection();
            
            ps = conn.prepareStatement("INSERT INTO plotmeDenied (idX, idZ, player, world) " +
					   "VALUES (?,?,?,?)");
            
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world.toLowerCase());
            
            ps.executeUpdate();
            conn.commit();
            
        } catch (SQLException ex) {
        	PlotMe_Core.self.getLogger().severe("Insert Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Insert Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void addPlotBid(String player, double bid, int idX, int idZ, String world)
    {
    	PreparedStatement ps = null;
        Connection conn;
        
    	//Auctions
        try 
        {
            conn = getConnection();
            
            ps = conn.prepareStatement("INSERT INTO plotmeAuctions (idX, idZ, player, world, bid) " +
					   "VALUES (?,?,?,?,?)");
            
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world.toLowerCase());
            ps.setDouble(5, bid);
            
            ps.executeUpdate();
            conn.commit();
            
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Insert Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Insert Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void addPlotComment(String[] comment, int commentid, int idX, int idZ, String world)
    {
    	PreparedStatement ps = null;
        Connection conn;
        
    	//Comments
        try 
        {
            conn = getConnection();
            
            ps = conn.prepareStatement("INSERT INTO plotmeComments (idX, idZ, commentid, player, comment, world) " +
					   "VALUES (?,?,?,?,?,?)");
            
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setInt(3, commentid);
            ps.setString(4, comment[0]);
            ps.setString(5, comment[1]);
            ps.setString(6, world.toLowerCase());
            
            ps.executeUpdate();
            conn.commit();
            
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Insert Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
            } catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Insert Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void deletePlot(int idX, int idZ, String world)
    {
        PreparedStatement ps = null;
        ResultSet set = null;
        try 
        {
            Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeComments WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3,	world);
            ps.executeUpdate();
            conn.commit();
            
            ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            
            ps = conn.prepareStatement("DELETE FROM plotmePlots WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Delete Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
                if (set != null) 
                {
                    set.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Delete Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void deletePlotComment(int idX, int idZ, int commentid, String world)
    {
        PreparedStatement ps = null;
        ResultSet set = null;
        try 
        {
            Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeComments WHERE idX = ? and idZ = ? and commentid = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setInt(3, commentid);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();
            
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Delete Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try {
                if (ps != null) 
                {
                    ps.close();
                }
                if (set != null) 
                {
                    set.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Delete Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void deletePlotAllowed(int idX, int idZ, String player, String world)
    {
        PreparedStatement ps = null;
        ResultSet set = null;
        
        try 
        {
            Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and player = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();
            
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Delete Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
                if (set != null) 
                {
                    set.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Delete Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void deletePlotDenied(int idX, int idZ, String player, String world)
    {
        PreparedStatement ps = null;
        ResultSet set = null;
        
        try 
        {
            Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeDenied WHERE idX = ? and idZ = ? and player = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();
            
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Delete Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
                if (set != null) 
                {
                    set.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Delete Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void deletePlotBid(int idX, int idZ, String player, String world)
    {
        PreparedStatement ps = null;
        ResultSet set = null;
        
        try 
        {
            Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeAuctions WHERE idX = ? and idZ = ? and player = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();
            
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Delete Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
                if (set != null) 
                {
                    set.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Delete Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void deleteAllPlotBids(int idX, int idZ, String world)
    {
        PreparedStatement ps = null;
        ResultSet set = null;
        
        try 
        {
            Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeAuctions WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Delete Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null) 
                {
                    ps.close();
                }
                if (set != null) 
                {
                    set.close();
                }
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Delete Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
    }
    
    public static Plot getPlot(String world, String id) 
    {
        Plot plot = null;
        PreparedStatement statementPlot = null;
        PreparedStatement statementAllowed = null;
        PreparedStatement statementDenied = null;
        PreparedStatement statementComment = null;
        ResultSet setPlots = null;
        ResultSet setAllowed = null;
        ResultSet setDenied = null;
		ResultSet setComments = null;

		int idX = PlotMeCoreManager.getIdX(id);
		int idZ = PlotMeCoreManager.getIdZ(id);
		
        try 
        {
            Connection conn = getConnection();

            statementPlot = conn.prepareStatement("SELECT * FROM plotmePlots WHERE LOWER(world) = ? AND idX = ? and idZ = ?");
            statementPlot.setString(1, world);
            statementPlot.setInt(2, idX);
            statementPlot.setInt(3, idZ);
            
            setPlots = statementPlot.executeQuery();

            if (setPlots.next())
            {
    			String owner = setPlots.getString("owner");
    			String biome = setPlots.getString("biome");
    			java.sql.Date expireddate = null;
    			try
    			{
    				expireddate = setPlots.getDate("expireddate");
    			}catch(Exception e){}
    			boolean finished = setPlots.getBoolean("finished");
    			HashSet<String> allowed = new HashSet<String>();
    			HashSet<String> denied = new HashSet<String>();
    			List<String[]> comments = new ArrayList<String[]>();
    			double customprice = setPlots.getDouble("customprice");
    			boolean forsale = setPlots.getBoolean("forsale");
    			String finisheddate = setPlots.getString("finisheddate");
    			boolean protect = setPlots.getBoolean("protected");
    			String currentbidder = setPlots.getString("currentbidder");
    			double currentbid = setPlots.getDouble("currentbid");
    			boolean auctionned = setPlots.getBoolean("auctionned");
    			String auctionneddate = setPlots.getString("auctionneddate");
    			
    			
    			statementAllowed = conn.prepareStatement("SELECT * FROM plotmeAllowed WHERE LOWER(world) = ? AND idX = ? AND idZ = ?");
    			statementAllowed.setString(1, world);
    			statementAllowed.setInt(2, idX);
    			statementAllowed.setInt(3, idZ);
                
    			setAllowed = statementAllowed.executeQuery();
    			
    			while (setAllowed.next())
    			{
    				allowed.add(setAllowed.getString("player"));
    			}
    			
    			if (setAllowed != null)
    				setAllowed.close();
    			
    			statementDenied = conn.prepareStatement("SELECT * FROM plotmeDenied WHERE LOWER(world) = ? AND idX = ? AND idZ = ?");
    			statementDenied.setString(1, world);
    			statementDenied.setInt(2, idX);
    			statementDenied.setInt(3, idZ);
    			
    			setDenied = statementDenied.executeQuery();
    			
    			while (setDenied.next())
    			{
    				denied.add(setDenied.getString("player"));
    			}
    			
    			if (setDenied != null)
    				setDenied.close();
    			
    			statementComment = conn.prepareStatement("SELECT * FROM plotmeComments WHERE LOWER(world) = ? AND idX = ? AND idZ = ?");
    			statementComment.setString(1, world);
    			statementComment.setInt(2, idX);
    			statementComment.setInt(3, idZ);
    			setComments = statementComment.executeQuery();
    			
    			while (setComments.next())
    			{
    				String[] comment = new String[2];
    				comment[0] = setComments.getString("player");
    				comment[1] = setComments.getString("comment");
    				comments.add(comment);
    			}
    			
    			plot = new Plot(owner, world, biome, expireddate, finished, allowed,
    					 comments, "" + idX + ";" + idZ, customprice, forsale, finisheddate, protect, 
    					 currentbidder, currentbid, auctionned, denied, auctionneddate);
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Plot load Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (statementPlot != null)
                	statementPlot.close();
                if (statementAllowed != null)
                	statementAllowed.close();
                if (statementComment != null)
                	statementComment.close();
                if (statementDenied != null)
                    statementDenied.close();
                if (setPlots != null)
                	setPlots.close();
                if (setComments != null)
                	setComments.close();
                if (setDenied != null)
                    setDenied.close();
                if (setAllowed != null)
                	setAllowed.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Plot load Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return plot;
    }
    
    /*public static HashMap<String, Plot> getPlots(String world) 
    {
        HashMap<String, Plot> ret = new HashMap<String, Plot>();
        Statement statementPlot = null;
        Statement statementAllowed = null;
        Statement statementDenied = null;
        Statement statementComment = null;
        ResultSet setPlots = null;
        ResultSet setAllowed = null;
        ResultSet setDenied = null;
		ResultSet setComments = null;

        try 
        {
            Connection conn = getConnection();

            statementPlot = conn.createStatement();
            setPlots = statementPlot.executeQuery("SELECT * FROM plotmePlots WHERE LOWER(world) = '" + world + "'");
            int size = 0;
            while (setPlots.next())
            {
            	size++;
    			int idX = setPlots.getInt("idX");
    			int idZ = setPlots.getInt("idZ");
    			String owner = setPlots.getString("owner");
    			String biome = setPlots.getString("biome");
    			java.sql.Date expireddate = null;
    			try
    			{
    				expireddate = setPlots.getDate("expireddate");
    			}catch(Exception e){}
    			boolean finished = setPlots.getBoolean("finished");
    			HashSet<String> allowed = new HashSet<String>();
    			HashSet<String> denied = new HashSet<String>();
    			List<String[]> comments = new ArrayList<String[]>();
    			double customprice = setPlots.getDouble("customprice");
    			boolean forsale = setPlots.getBoolean("forsale");
    			String finisheddate = setPlots.getString("finisheddate");
    			boolean protect = setPlots.getBoolean("protected");
    			String currentbidder = setPlots.getString("currentbidder");
    			double currentbid = setPlots.getDouble("currentbid");
    			boolean auctionned = setPlots.getBoolean("auctionned");
    			String auctionneddate = setPlots.getString("auctionneddate");
    			
    			
    			statementAllowed = conn.createStatement();
    			setAllowed = statementAllowed.executeQuery("SELECT * FROM plotmeAllowed WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND LOWER(world) = '" + world + "'");
    			
    			while (setAllowed.next())
    			{
    				allowed.add(setAllowed.getString("player"));
    			}
    			
    			if (setAllowed != null)
    				setAllowed.close();
    			
    			statementDenied = conn.createStatement();
    			setDenied = statementDenied.executeQuery("SELECT * FROM plotmeDenied WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND LOWER(world) = '" + world + "'");
    			
    			while (setDenied.next())
    			{
    				denied.add(setDenied.getString("player"));
    			}
    			
    			if (setDenied != null)
    				setDenied.close();
    			
    			statementComment = conn.createStatement();
    			setComments = statementComment.executeQuery("SELECT * FROM plotmeComments WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND LOWER(world) = '" + world + "'");
    			
    			while (setComments.next())
    			{
    				String[] comment = new String[2];
    				comment[0] = setComments.getString("player");
    				comment[1] = setComments.getString("comment");
    				comments.add(comment);
    			}
    			
    			Plot plot = new Plot(owner, world, biome, expireddate, finished, allowed,
    					 comments, "" + idX + ";" + idZ, customprice, forsale, finisheddate, protect, 
    					 currentbidder, currentbid, auctionned, denied, auctionneddate);
                ret.put("" + idX + ";" + idZ, plot);
            }
            PlotMe_Core.self.getLogger().info(size + " plots loaded in " + world);
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("Load Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (statementPlot != null)
                	statementPlot.close();
                if (statementAllowed != null)
                	statementAllowed.close();
                if (statementComment != null)
                	statementComment.close();
                if (statementDenied != null)
                    statementDenied.close();
                if (setPlots != null)
                	setPlots.close();
                if (setComments != null)
                	setComments.close();
                if (setDenied != null)
                    setDenied.close();
                if (setAllowed != null)
                	setAllowed.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("Load Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return ret;
    }    */
    
    public static long getPlotCount(String world) 
    {
        PreparedStatement ps = null;
        ResultSet setNbPlots = null;
        long nbplots = 0;
        
        try 
        {
            Connection conn = getConnection();

            ps = conn.prepareStatement("SELECT Count(*) as NbPlot FROM plotmePlots WHERE LOWER(world) = ?");
            ps.setString(1, world);
            
            setNbPlots = ps.executeQuery();
            
            if(setNbPlots.next())
            {
            	nbplots = setNbPlots.getLong(1);
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("PlotCount Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null)
                	ps.close();
                if (setNbPlots != null)
                	setNbPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("PlotCount Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return nbplots;
    }
    
    
    public static int getPlotCount(String world, String owner) 
    {
        PreparedStatement ps = null;
        ResultSet setNbPlots = null;
        int nbplots = 0;
        
        try 
        {
            Connection conn = getConnection();

            ps = conn.prepareStatement("SELECT Count(*) as NbPlot FROM plotmePlots WHERE LOWER(world) = ? AND owner = ?");
            ps.setString(1, world);
            ps.setString(2, owner);
            
            setNbPlots = ps.executeQuery();
            
            if(setNbPlots.next())
            {
            	nbplots = setNbPlots.getInt(1);
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("PlotCount Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null)
                	ps.close();
                if (setNbPlots != null)
                	setNbPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("PlotCount Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return nbplots;
    }
    
    
    
    public static long getFinishedPlotCount(String world) 
    {
        PreparedStatement ps = null;
        ResultSet setNbPlots = null;
        long nbplots = 0;
        
        try 
        {
            Connection conn = getConnection();

            ps = conn.prepareStatement("SELECT Count(*) as NbPlot FROM plotmePlots WHERE LOWER(world) = ? AND finished <> 0");
            ps.setString(1, world);
            
            setNbPlots = ps.executeQuery();
            
            if(setNbPlots.next())
            {
            	nbplots = setNbPlots.getLong(1);
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("FinishedPlotCount Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null)
                	ps.close();
                if (setNbPlots != null)
                	setNbPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("FinishedPlotCount Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return nbplots;
    }
    
    public static long getExpiredPlotCount(String world) 
    {
        PreparedStatement ps = null;
        ResultSet setNbPlots = null;
        long nbplots = 0;
        
        try 
        {
            Connection conn = getConnection();

            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.util.Date utilDate = cal.getTime();
            java.sql.Date sqlDate = new Date(utilDate.getTime());
            
            ps = conn.prepareStatement("SELECT Count(*) as NbPlot FROM plotmePlots WHERE LOWER(world) = ? AND expireddate < ?");
            ps.setString(1, world);
            ps.setDate(2, sqlDate);
            
            setNbPlots = ps.executeQuery();
            
            if(setNbPlots.next())
            {
            	nbplots = setNbPlots.getLong(1);
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("ExpiredPlotCount Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (ps != null)
                	ps.close();
                if (setNbPlots != null)
                	setNbPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("ExpiredPlotCount Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return nbplots;
    }
    
    public static List<Plot> getDonePlots(String world, int Page, int NbPerPage) 
    {
    	List<Plot> ret = new ArrayList<Plot>();
        PreparedStatement statementPlot = null;
        ResultSet setPlots = null;

        try 
        {
            Connection conn = getConnection();

            statementPlot = conn.prepareStatement("SELECT idX, idZ, owner, finisheddate FROM plotmePlots WHERE LOWER(world) = ? AND finished <> 0 ORDER BY finisheddate LIMIT ?, ?");
            statementPlot.setString(1, world);
            statementPlot.setInt(2, NbPerPage * (Page-1));
            statementPlot.setInt(3, NbPerPage);
                        
            setPlots = statementPlot.executeQuery();

            while (setPlots.next())
            {
    			int idX = setPlots.getInt("idX");
    			int idZ = setPlots.getInt("idZ");
    			String owner = setPlots.getString("owner");

    			String finisheddate = setPlots.getString("finisheddate");
    			    			
    			Plot plot = new Plot();
    			plot.owner = owner;
    			plot.id = idX + ";" + idZ;
    			plot.finisheddate = finisheddate;
    				
                ret.add(plot);
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("DonePlots Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (statementPlot != null)
                	statementPlot.close();
                if (setPlots != null)
                	setPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("DonePlots Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return ret;
    }
    
    public static List<Plot> getExpiredPlots(String world, int Page, int NbPerPage) 
    {
    	List<Plot> ret = new ArrayList<Plot>();
        PreparedStatement statementPlot = null;
        ResultSet setPlots = null;

        try 
        {
            Connection conn = getConnection();

            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.util.Date utilDate = cal.getTime();
            java.sql.Date sqlDate = new Date(utilDate.getTime());

            statementPlot = conn.prepareStatement("SELECT idX, idZ, owner, expireddate FROM plotmePlots WHERE LOWER(world) = ? AND protected = 0 AND expireddate < ? ORDER BY expireddate LIMIT ?, ?");
            statementPlot.setString(1, world);
            statementPlot.setDate(2, sqlDate);
            statementPlot.setInt(3, NbPerPage * (Page-1));
            statementPlot.setInt(4, NbPerPage);
                        
            setPlots = statementPlot.executeQuery();

            while (setPlots.next())
            {
    			int idX = setPlots.getInt("idX");
    			int idZ = setPlots.getInt("idZ");
    			String owner = setPlots.getString("owner");

    			java.sql.Date expireddate = null;
    			try
    			{
    				expireddate = setPlots.getDate("expireddate");
    			}catch(Exception e){}
    			    			
    			Plot plot = new Plot();
    			plot.owner = owner;
    			plot.id = idX + ";" + idZ;
    			plot.expireddate = expireddate;
    				
                ret.add(plot);
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("ExpiredPlots Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (statementPlot != null)
                	statementPlot.close();
                if (setPlots != null)
                	setPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("ExpiredPlots Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return ret;
    }
    
    public static Plot getExpiredPlot(String world) 
    {
        PreparedStatement statementPlot = null;
        ResultSet setPlots = null;

        try 
        {
            Connection conn = getConnection();

            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.util.Date utilDate = cal.getTime();
            java.sql.Date sqlDate = new Date(utilDate.getTime());
            
            statementPlot = conn.prepareStatement("SELECT idX, idZ, owner, expireddate FROM plotmePlots WHERE LOWER(world) = ? AND protected = 0 AND expireddate < ? ORDER BY expireddate LIMIT 1");
            statementPlot.setString(1, world);
            statementPlot.setDate(2, sqlDate);
                        
            setPlots = statementPlot.executeQuery();

            if(setPlots.next())
            {
    			int idX = setPlots.getInt("idX");
    			int idZ = setPlots.getInt("idZ");
    			String owner = setPlots.getString("owner");

    			java.sql.Date expireddate = null;
    			try
    			{
    				expireddate = setPlots.getDate("expireddate");
    			}catch(Exception e){}
    			    			
    			Plot plot = new Plot();
    			plot.owner = owner;
    			plot.id = idX + ";" + idZ;
    			plot.expireddate = expireddate;
    				
                return plot;
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("ExpiredPlots Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (statementPlot != null)
                	statementPlot.close();
                if (setPlots != null)
                	setPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("ExpiredPlots Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return null;
    }
    
    public static List<Plot> getPlayerPlots(String owner) 
    {
    	List<Plot> ret = new ArrayList<Plot>();
        PreparedStatement statementPlot = null;
        PreparedStatement statementAllowed = null;
        PreparedStatement statementDenied = null;
        PreparedStatement statementComment = null;
        ResultSet setPlots = null;
        ResultSet setAllowed = null;
        ResultSet setDenied = null;
		ResultSet setComments = null;

        try 
        {
            Connection conn = getConnection();

            statementPlot = conn.prepareStatement("SELECT A.* FROM plotmePlots A LEFT JOIN plotmeAllowed B ON A.idX = B.idX AND A.idZ = B.idZ AND A.world = B.world " +
            						" WHERE owner LIKE ? OR B.player LIKE ? ORDER BY A.world");
            statementPlot.setString(1, owner);
            statementPlot.setString(2, owner);
                        
            setPlots = statementPlot.executeQuery();

            while (setPlots.next())
            {
            	int idX = setPlots.getInt("idX");
    			int idZ = setPlots.getInt("idZ");
    			String biome = setPlots.getString("biome");
    			java.sql.Date expireddate = null;
    			try
    			{
    				expireddate = setPlots.getDate("expireddate");
    			}catch(Exception e){}
    			boolean finished = setPlots.getBoolean("finished");
    			HashSet<String> allowed = new HashSet<String>();
    			HashSet<String> denied = new HashSet<String>();
    			List<String[]> comments = new ArrayList<String[]>();
    			double customprice = setPlots.getDouble("customprice");
    			boolean forsale = setPlots.getBoolean("forsale");
    			String finisheddate = setPlots.getString("finisheddate");
    			boolean protect = setPlots.getBoolean("protected");
    			String currentbidder = setPlots.getString("currentbidder");
    			double currentbid = setPlots.getDouble("currentbid");
    			boolean auctionned = setPlots.getBoolean("auctionned");
    			String auctionneddate = setPlots.getString("auctionneddate");
    			String world = setPlots.getString("world");
    			
    			
    			statementAllowed = conn.prepareStatement("SELECT * FROM plotmeAllowed WHERE LOWER(world) = ? AND idX = ? AND idZ = ?");
    			statementAllowed.setString(1, world);
    			statementAllowed.setInt(2, idX);
    			statementAllowed.setInt(3, idZ);
                
    			setAllowed = statementAllowed.executeQuery();
    			
    			while (setAllowed.next())
    			{
    				allowed.add(setAllowed.getString("player"));
    			}
    			
    			if (setAllowed != null)
    				setAllowed.close();
    			
    			statementDenied = conn.prepareStatement("SELECT * FROM plotmeDenied WHERE LOWER(world) = ? AND idX = ? AND idZ = ?");
    			statementDenied.setString(1, world);
    			statementDenied.setInt(2, idX);
    			statementDenied.setInt(3, idZ);
    			
    			setDenied = statementDenied.executeQuery();
    			
    			while (setDenied.next())
    			{
    				denied.add(setDenied.getString("player"));
    			}
    			
    			if (setDenied != null)
    				setDenied.close();
    			
    			statementComment = conn.prepareStatement("SELECT * FROM plotmeComments WHERE LOWER(world) = ? AND idX = ? AND idZ = ?");
    			statementComment.setString(1, world);
    			statementComment.setInt(2, idX);
    			statementComment.setInt(3, idZ);
    			setComments = statementComment.executeQuery();
    			
    			while (setComments.next())
    			{
    				String[] comment = new String[2];
    				comment[0] = setComments.getString("player");
    				comment[1] = setComments.getString("comment");
    				comments.add(comment);
    			}
    			
    			Plot plot = new Plot(owner, world, biome, expireddate, finished, allowed,
    					 comments, "" + idX + ";" + idZ, customprice, forsale, finisheddate, protect, 
    					 currentbidder, currentbid, auctionned, denied, auctionneddate);
    				
                ret.add(plot);
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("DonePlots Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (statementPlot != null)
                	statementPlot.close();
                if (setPlots != null)
                	setPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("DonePlots Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return ret;
    }
    
    public static List<Plot> getOwnedPlots(String w, String owner) 
    {
    	List<Plot> ret = new ArrayList<Plot>();
        PreparedStatement statementPlot = null;
        PreparedStatement statementAllowed = null;
        PreparedStatement statementDenied = null;
        PreparedStatement statementComment = null;
        ResultSet setPlots = null;
        ResultSet setAllowed = null;
        ResultSet setDenied = null;
		ResultSet setComments = null;

        try 
        {
            Connection conn = getConnection();

            statementPlot = conn.prepareStatement("SELECT * FROM plotmePlots WHERE world LIKE ? AND owner LIKE ? ORDER BY world");
            statementPlot.setString(1, w);
            statementPlot.setString(2, owner);
                        
            setPlots = statementPlot.executeQuery();

            while (setPlots.next())
            {
            	int idX = setPlots.getInt("idX");
    			int idZ = setPlots.getInt("idZ");
    			String biome = setPlots.getString("biome");
    			java.sql.Date expireddate = null;
    			try
    			{
    				expireddate = setPlots.getDate("expireddate");
    			}catch(Exception e){}
    			boolean finished = setPlots.getBoolean("finished");
    			HashSet<String> allowed = new HashSet<String>();
    			HashSet<String> denied = new HashSet<String>();
    			List<String[]> comments = new ArrayList<String[]>();
    			double customprice = setPlots.getDouble("customprice");
    			boolean forsale = setPlots.getBoolean("forsale");
    			String finisheddate = setPlots.getString("finisheddate");
    			boolean protect = setPlots.getBoolean("protected");
    			String currentbidder = setPlots.getString("currentbidder");
    			double currentbid = setPlots.getDouble("currentbid");
    			boolean auctionned = setPlots.getBoolean("auctionned");
    			String auctionneddate = setPlots.getString("auctionneddate");
    			String world = setPlots.getString("world");
    			
    			
    			statementAllowed = conn.prepareStatement("SELECT * FROM plotmeAllowed WHERE LOWER(world) = ? AND idX = ? AND idZ = ?");
    			statementAllowed.setString(1, world);
    			statementAllowed.setInt(2, idX);
    			statementAllowed.setInt(3, idZ);
                
    			setAllowed = statementAllowed.executeQuery();
    			
    			while (setAllowed.next())
    			{
    				allowed.add(setAllowed.getString("player"));
    			}
    			
    			if (setAllowed != null)
    				setAllowed.close();
    			
    			statementDenied = conn.prepareStatement("SELECT * FROM plotmeDenied WHERE LOWER(world) = ? AND idX = ? AND idZ = ?");
    			statementDenied.setString(1, world);
    			statementDenied.setInt(2, idX);
    			statementDenied.setInt(3, idZ);
    			
    			setDenied = statementDenied.executeQuery();
    			
    			while (setDenied.next())
    			{
    				denied.add(setDenied.getString("player"));
    			}
    			
    			if (setDenied != null)
    				setDenied.close();
    			
    			statementComment = conn.prepareStatement("SELECT * FROM plotmeComments WHERE LOWER(world) = ? AND idX = ? AND idZ = ?");
    			statementComment.setString(1, world);
    			statementComment.setInt(2, idX);
    			statementComment.setInt(3, idZ);
    			setComments = statementComment.executeQuery();
    			
    			while (setComments.next())
    			{
    				String[] comment = new String[2];
    				comment[0] = setComments.getString("player");
    				comment[1] = setComments.getString("comment");
    				comments.add(comment);
    			}
    			
    			Plot plot = new Plot(owner, world, biome, expireddate, finished, allowed,
    					 comments, "" + idX + ";" + idZ, customprice, forsale, finisheddate, protect, 
    					 currentbidder, currentbid, auctionned, denied, auctionneddate);
    				
                ret.add(plot);
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("DonePlots Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (statementPlot != null)
                	statementPlot.close();
                if (setPlots != null)
                	setPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("DonePlots Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return ret;
    }
    
    public static String getFirstWorld(String owner) 
    {
        PreparedStatement statementPlot = null;
        ResultSet setPlots = null;

        try 
        {
            Connection conn = getConnection();

            statementPlot = conn.prepareStatement("SELECT world FROM plotmePlots WHERE owner = ? LIMIT 1");
            statementPlot.setString(1, owner);
                        
            setPlots = statementPlot.executeQuery();

            if (setPlots.next())
            {
    			return setPlots.getString("world");
            }
        } 
        catch (SQLException ex) 
        {
        	PlotMe_Core.self.getLogger().severe("DonePlots Exception :");
        	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
        } 
        finally 
        {
            try 
            {
                if (statementPlot != null)
                	statementPlot.close();
                if (setPlots != null)
                	setPlots.close();
            } 
            catch (SQLException ex) 
            {
            	PlotMe_Core.self.getLogger().severe("DonePlots Exception (on close) :");
            	PlotMe_Core.self.getLogger().severe("  " + ex.getMessage());
            }
        }
        return "";
    }

}
