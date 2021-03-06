package org.weasis.core.api.gui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.osgi.service.prefs.Preferences;
import org.weasis.core.api.gui.Insertable.Type;
import org.weasis.core.api.service.BundlePreferences;
import org.weasis.core.api.service.BundleTools;

public class InsertableUtil {
    public final static String ALL_BUNDLE = "weasis"; //$NON-NLS-1$
    public final static String ALL = "all"; //$NON-NLS-1$

    private InsertableUtil() {

    }

    public static void sortInsertable(List<? extends Insertable> list) {
        Collections.sort(list, new Comparator<Insertable>() {

            @Override
            public int compare(Insertable o1, Insertable o2) {
                int val1 = o1.getComponentPosition();
                int val2 = o2.getComponentPosition();
                return val1 < val2 ? -1 : (val1 == val2 ? 0 : 1);
            }
        });
    }

    public static void applyPreferences(List<? extends Insertable> list, Preferences prefs, String bundleName,
        String componentName, Type type) {
        if (list != null && prefs != null && bundleName != null && componentName != null) {
            Preferences prefNode = prefs.node(componentName).node(type.name().toLowerCase() + "s"); //$NON-NLS-1$
            synchronized (list) {
                for (Insertable c : list) {
                    if (!Type.EMPTY.equals(c.getType())) {
                        String nodeName = getCName(c.getClass());
                        String key = "visible"; //$NON-NLS-1$
                        Preferences node = prefNode.node(nodeName);
                        String valString = node.get(key, null);
                        // If not specify, value is true
                        boolean val = true;
                        if (valString == null) {
                            val =
                                getBooleanProperty(BundleTools.SYSTEM_PREFERENCES, bundleName, componentName, nodeName,
                                    key, val);
                        } else if (valString.equalsIgnoreCase("false")) { //$NON-NLS-1$
                            val = false;
                        }
                        c.setComponentEnabled(val);

                        key = "cPosition"; //$NON-NLS-1$
                        valString = node.get(key, null);
                        // If not specify, value is true
                        int index = c.getComponentPosition();
                        if (valString == null) {
                            index =
                                getIntProperty(BundleTools.SYSTEM_PREFERENCES, bundleName, componentName, nodeName,
                                    key, index);
                        } else {
                            try {
                                index = Integer.parseInt(valString);
                            } catch (NumberFormatException ignore) {
                                // return the default value
                            }
                        }
                        c.setComponentPosition(index);
                    }
                }
            }
        }
    }

    public static void savePreferences(List<? extends Insertable> list, Preferences prefs, Type type) {
        if (list != null && prefs != null) {
            Preferences prefNode = prefs.node(type.name().toLowerCase() + "s"); //$NON-NLS-1$
            synchronized (list) {
                for (Insertable c : list) {
                    if (!Type.EMPTY.equals(c.getType())) {
                        String cname = getCName(c.getClass());
                        Preferences node = prefNode.node(cname);
                        BundlePreferences.putBooleanPreferences(node, "visible", c.isComponentEnabled()); //$NON-NLS-1$
                        BundlePreferences.putIntPreferences(node, "cPosition", c.getComponentPosition()); //$NON-NLS-1$
                    }
                }
            }
        }
    }

    public static boolean getBooleanProperty(Properties props, String bundleName, String className,
        String componentName, String key, boolean def) {
        if (props != null && bundleName != null && className != null && key != null) {
            for (String bundle : new String[] { bundleName, ALL_BUNDLE }) {
                for (String cl : new String[] { className, ALL }) {
                    StringBuilder buf = new StringBuilder(bundle);
                    buf.append('.');
                    buf.append(cl);
                    buf.append('.');
                    buf.append(componentName);
                    buf.append('.');
                    buf.append(key);
                    final String value = props.getProperty(buf.toString());
                    if (value != null) {
                        if (value.equalsIgnoreCase("true")) { //$NON-NLS-1$
                            return true;
                        } else if (value.equalsIgnoreCase("false")) { //$NON-NLS-1$
                            return false;
                        }
                    }
                }
            }
        }
        return def;
    }

    private static int getIntProperty(Properties props, String bundleName, String className, String componentName,
        String key, int def) {
        if (props != null && bundleName != null && className != null && key != null) {
            for (String bundle : new String[] { bundleName, ALL_BUNDLE }) {
                for (String cl : new String[] { className, ALL }) {
                    StringBuilder buf = new StringBuilder(bundle);
                    buf.append('.');
                    buf.append(cl);
                    buf.append('.');
                    buf.append(componentName);
                    buf.append('.');
                    buf.append(key);
                    final String value = props.getProperty(buf.toString());
                    if (value != null) {
                        try {
                            return Integer.parseInt(value);
                        } catch (NumberFormatException ignore) {
                            // return the default value
                        }
                    }
                }
            }
        }
        return def;
    }

    public static String getCName(Class<?> clazz) {
        if (clazz != null) {
            return clazz.getSimpleName().toLowerCase();
        }
        return ""; //$NON-NLS-1$
    }
}
