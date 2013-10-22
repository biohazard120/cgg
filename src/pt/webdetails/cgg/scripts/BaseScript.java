/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg.scripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import pt.webdetails.cgg.datasources.DatasourceFactory;

/**
 *
 * @author pdpi
 */
public abstract class BaseScript implements Script {

    protected static final Log logger = LogFactory.getLog(BaseScript.class);
    protected GenericPath source;
    protected Scriptable   scope;
    
    BaseScript() {
    }

    BaseScript(GenericPath source) {
        this.source = source;
    }

    public void initializeObjects() {
        ContextFactory.getGlobal().enter();
        Object wrappedFactory = Context.javaToJS(new DatasourceFactory(), scope);
        ScriptableObject.putProperty(scope, "datasourceFactory", wrappedFactory);
    }

    public void setScope(Scriptable scope) {
        this.scope = scope;
        
        if(scope instanceof BaseScope) 
        {
            ((BaseScope)scope).setBasePath(source != null ? source.getBasePath() : null);
        }
        initializeObjects();
    }

    protected void executeScript(Map<String, Object> params) {
        Context cx = Context.getCurrentContext();
        // env.js has methods that pass the 64k Java limit, so we can't compile
        // to bytecode. Interpreter mode to the rescue!
        cx.setOptimizationLevel(-1);
        cx.setLanguageVersion(Context.VERSION_1_7);

        Object wrappedParams;
        if (params != null) {
            wrappedParams = Context.javaToJS(params, scope);
        } else {
            wrappedParams = Context.javaToJS(new HashMap<String, Object>(), scope);
        }
        ScriptableObject.defineProperty(scope, "params", wrappedParams, 0);

        try {
            cx.evaluateReader(scope, source.getReader(), source.getName(), 1, null);
        } catch (IOException ex) {
            logger.error("Failed to read " + source + ": " + ex.toString());
        }
    }
}