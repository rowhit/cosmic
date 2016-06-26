//

//

package com.cloud.utils.cisco.n1kv.vsm;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class VsmOkResponse extends VsmResponse {

    VsmOkResponse(final String response) {
        super(response);
        initialize();
    }

    @Override
    protected void parse(final Element root) {
        final NodeList list = root.getElementsByTagName("nf:rpc-error");
        if (list.getLength() == 0) {
            // No rpc-error tag; means response was ok.
            assert (root.getElementsByTagName("nf:ok").getLength() > 0);
            _responseOk = true;
        } else {
            parseError(list.item(0));
            _responseOk = false;
        }
    }
}
