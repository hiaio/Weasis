#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.weasis.core.api.gui.util.JMVUtils;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.ui.util.WtoolBar;

public class SampleToolBar<E extends ImageElement> extends WtoolBar {

    protected SampleToolBar() {
        super("Sample Toolbar", 500);

        final JButton helpButton = new JButton();
        helpButton.setToolTipText("User Guide");
        helpButton.setIcon(new ImageIcon(SampleToolBar.class.getResource("/icon/32x32/help-browser.png"))); //${symbol_dollar}NON-NLS-1${symbol_dollar}
        helpButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof Component) {
                    URL url;
                    try {
                        url = new URL("http://www.dcm4che.org/confluence/display/WEA/User+Guide"); //${symbol_dollar}NON-NLS-1${symbol_dollar}
                        JMVUtils.OpenInDefaultBrowser((Component) e.getSource(), url);
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        add(helpButton);

    }

}
