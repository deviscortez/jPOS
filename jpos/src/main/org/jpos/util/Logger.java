package org.jpos.util;

import java.io.*;
import java.util.*;

/**
 * Peer class Logger forwards LogEvents generated by LogSources 
 * to LogListeners.
 * <br>
 * This little <a href="/doc/LoggerGuide.html">tutorial</a>
 * give you additional information on how to extend the jPOS's
 * Logger subsystem.
 *
 * @author apr@cs.com.uy
 * @version $Id$
 * @see LogEvent
 * @see LogSource
 * @see LogListener
 * @see Loggeable
 * @see SimpleLogListener
 * @see RotateLogListener
 * @see AntiHog
 */
public class Logger {
    String name;
    Vector listeners;
    AntiHog antiHog;

    public Logger () {
	super();
	listeners = new Vector ();
	antiHog   = null;
	name = "";
    }
    public void addListener (LogListener l) {
	listeners.add (l);
    }
    public void removeListener (LogListener l) {
	listeners.remove (l);
    }
    public void setAntiHog (AntiHog antiHog) {
	this.antiHog = antiHog;
    }
    private void checkAntiHog (LogEvent ev) {
	if (antiHog != null) {
	    int p = antiHog.nice ();
	    if (p > 0)
		ev.addMessage ("<antiHog penalty=\""+p+"ms\"/>");
	}
    }
    public static void log (LogEvent ev) {
	Logger l = ((LogSource) ev.getSource()).getLogger();
	if (l != null) {
	    l.checkAntiHog (ev);
	    Iterator i = l.listeners.iterator();
	    while (i.hasNext()) 
		((LogListener) i.next()).log (ev);
	}
    }
    /**
     * associates this Logger with a name using NameRegistrar
     * @param name name to register
     * @see NameRegistrar
     */
    public void setName (String name) {
	this.name = name;
	NameRegistrar.register ("logger."+name, this);
    }
    /**
     * @return logger instance with given name. Creates one if necessary
     * @see NameRegistrar
     */
    public static Logger getLogger (String name) {
	Logger l;
	try {
	    l = (Logger) NameRegistrar.get ("logger."+name);
	} catch (NameRegistrar.NotFoundException e) {
	    l = new Logger();
	    l.setName (name);
	}
	return l;
    }
    /**
     * @return this logger's name ("" if no name was set)
     */
    public String getName() {
	return this.name;
    }
}
