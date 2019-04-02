package gov.usgs.locator;

import java.util.Date;
import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Simple(r) log formatter for java.util.logging messages.
 * 
 * Outputs unique dates once, with all messages sharing that time tab indented below.
 * 
 * Example Format:
 * <pre>
 * Wed Sep 30 19:31:48 GMT 2009
 * INFO    Exit code=0
 * Wed Sep 30 19:32:52 GMT 2009
 * INFO    [polldir] duplicate product id=urn:earthquake-usgs-gov:shakemap-scraper:global:2009medd:1
 * Wed Sep 30 19:32:53 GMT 2009
 * INFO    [polldir] received urn:earthquake-usgs-gov:shakemap-scraper:global:2009medd:1
 * INFO    [losspager] filtering type 'shakemap-scraper', not allowed
 * INFO    [logging_client] received urn:earthquake-usgs-gov:shakemap-scraper:global:2009medd:1
 * INFO    [shakemap] received urn:earthquake-usgs-gov:shakemap-scraper:global:2009medd:1
 * </pre>
 * 
 */
public class SimpleLogFormatter extends Formatter {

    /** Milliseconds in a second. */
    public static final long MILLIS_PER_SECOND = 1000;

    /** When the last LogRecord was processed. */
    private long lastMillis = 0;

    /** Default constructor. */
    public SimpleLogFormatter() {
    }

    /**
     * Format a LogRecord for output.
     * 
     * @param record
     *            LogRecord to format.
     * @return formatted LogRecord as String.
     */
    public final String format(final LogRecord record) {
        StringBuffer buf = new StringBuffer();
        Format formatter = new SimpleDateFormat("yyyyMMdd_HH:mm:ss:");

        // chop to nearest second, not outputting millis...
        long millis = (record.getMillis() / MILLIS_PER_SECOND) * MILLIS_PER_SECOND;

        if (lastMillis == 0) {
          // first run...
          lastMillis = millis;

          buf.append(formatter.format(new Date(millis))).append(" Startup\n");
        } else if (millis != lastMillis) {
            lastMillis = millis;
            
            // add date
            buf.append(formatter.format(new Date(lastMillis))).append("\n");
        }

        // add log message
        buf.append(new String().format("[ %-7s ] ", record.getLevel().getLocalizedName()));
        buf.append(new String().format("[ %-10s ] ", record.getSourceMethodName()));
        buf.append(record.getMessage());
        buf.append("\n");

        // output any associated exception
        Throwable thrown = record.getThrown();
        if (thrown != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            thrown.printStackTrace(new PrintStream(out, true));
            buf.append(new String(out.toByteArray()));
        }

        return buf.toString();
    }

}