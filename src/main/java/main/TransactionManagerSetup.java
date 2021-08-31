package main;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
//import java.util.logging.Logger;

/**
 * Provides a database connection pool with the Bitronix JTA transaction
 * manager (http://docs.codehaus.org/display/BTM/Home).
 * <p>
 * Hibernate will look up the datasource and <code>UserTransaction</code> through
 * JNDI, that's why you also need a <code>jndi.properties</code> file. A minimal
 * JNDI context is bundled with and started by Bitronix.
 * </p>
 */
public class TransactionManagerSetup {

    public static final String DATASOURCE_NAME = "dataBase";

   /* private static final Logger logger =
        Logger.getLogger(TransactionManagerSetup.class.getName());*/

    protected final Context context = new InitialContext();
    protected final PoolingDataSource datasource;
    public final DatabaseProduct databaseProduct;

    public TransactionManagerSetup(DatabaseProduct databaseProduct) throws Exception {
        this(databaseProduct, null);
    }

    public TransactionManagerSetup(DatabaseProduct databaseProduct,
                                   String connectionURL) throws Exception {

       // logger.warn("Starting database connection pool");

       // logger.warn("Setting stable unique identifier for transaction recovery");
        TransactionManagerServices.getConfiguration().setServerId("try");

        //logger.warn("Disabling JMX binding of manager in unit tests");
        TransactionManagerServices.getConfiguration().setDisableJmx(true);

       // logger.warn("Disabling transaction logging for unit tests");
        TransactionManagerServices.getConfiguration().setJournal("null");

       // logger.warn("Disabling warnings when the database isn't accessed in a transaction");
        TransactionManagerServices.getConfiguration().setWarnAboutZeroResourceTransaction(false);

       // logger.warn("Creating connection pool");
        datasource = new PoolingDataSource();
        datasource.setUniqueName(DATASOURCE_NAME);
        datasource.setMinPoolSize(1);
        datasource.setMaxPoolSize(5);
        datasource.setPreparedStatementCacheSize(10);

        // Our locking/versioning tests assume READ COMMITTED transaction
        // isolation. This is not the default on MySQL InnoDB, so we set
        // it here explicitly.
        datasource.setIsolationLevel("READ_COMMITTED");

        // Hibernate's SQL schema generator calls connection.setAutoCommit(true)
        // and we use auto-commit mode when the EntityManager is in suspended
        // mode and not joined with a transaction.
        datasource.setAllowLocalTransactions(true);

      //  logger.info("Setting up database connection: " + databaseProduct);
        this.databaseProduct = databaseProduct;
        databaseProduct.configuration.configure(datasource, connectionURL);
//
       // logger.warn("Initializing transaction and resource management");
        datasource.init();
    }

    public Context getNamingContext() {
        return context;
    }

    public UserTransaction getUserTransaction() {
        try {            
            return (UserTransaction) getNamingContext()
                .lookup("java:comp/UserTransaction");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public DataSource getDataSource() {
        try {
            return (DataSource) getNamingContext().lookup(DATASOURCE_NAME);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void rollback() {
        UserTransaction tx = getUserTransaction();
        try {
            if (tx.getStatus() == Status.STATUS_ACTIVE ||
                tx.getStatus() == Status.STATUS_MARKED_ROLLBACK)
                tx.rollback();
        } catch (Exception ex) {
            System.err.println("Rollback of transaction failed, trace follows!");
            ex.printStackTrace(System.err);
        }
    }

    public void stop() throws Exception {
       // logger.warn("Stopping database connection pool");
        datasource.close();
        TransactionManagerServices.getTransactionManager().shutdown();
    }

}