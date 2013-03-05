/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.datasources;

import java.io.IOException;
import javax.swing.table.TableModel;

public interface Exporter
{
  public String export(final TableModel tableModel) throws IOException;
}
