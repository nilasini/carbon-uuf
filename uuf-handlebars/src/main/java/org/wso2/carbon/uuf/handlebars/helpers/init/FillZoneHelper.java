package org.wso2.carbon.uuf.handlebars.helpers.init;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import org.wso2.carbon.uuf.core.Renderable;
import org.wso2.carbon.uuf.core.UUFException;
import org.wso2.carbon.uuf.handlebars.HbsInitRenderable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.wso2.carbon.uuf.handlebars.HbsInitRenderable.DATA_KEY_ZONES;

public class FillZoneHelper implements Helper<String> {

    public static final String HELPER_NAME = "fillZone";

    @Override
    public CharSequence apply(String zoneName, Options options) throws IOException {
        Map<String, Renderable> zones = options.data(DATA_KEY_ZONES);

        // We will prepend the source with spaces and enters to make the line numbers correct.
        int line = options.fn.position()[0];
        String source;
        if (line > 0) {
            String crlf = System.lineSeparator();
            int col = options.fn.position()[1];
            StringBuilder sb = new StringBuilder(col + (line - 1) * crlf.length());
            for (int i = 0; i < line - 1; i++) {
                sb.append(crlf);
            }
            for (int i = 0; i < col; i++) {
                sb.append(' ');
            }

            sb.append(options.fn.text());
            source = sb.toString();
        } else {
            //TODO: line should be larger than 0 but due to some reason it's < 0. this is a temp workaround
            source = options.fn.text();
        }
        if (zones == null) {
            zones = new HashMap<>();
            options.data(DATA_KEY_ZONES, zones);
        }
        TemplateSource templateSource = new StringTemplateSource(options.fn.filename(), source);
        HbsInitRenderable fillZoneRenderable = new HbsInitRenderable(templateSource, Optional.empty());
        if (fillZoneRenderable.getFillingZones().size() > 0) {
            throw new UUFException("Calling '" + HELPER_NAME + "' helper inside of another '" + HELPER_NAME +
                                           "' helper block is prohibited.");
        }
        zones.put(zoneName, fillZoneRenderable);
        return "";
    }
}
