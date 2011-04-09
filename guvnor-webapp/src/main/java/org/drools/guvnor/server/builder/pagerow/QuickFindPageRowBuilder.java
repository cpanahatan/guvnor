/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.builder.pagerow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.server.AssetItemFilter;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.QueryPageRowCreator;
import org.drools.repository.AssetItem;
import org.drools.repository.RepositoryFilter;

public class QuickFindPageRowBuilder {

    public List<QueryPageRow> createRows(final PageRequest pageRequest,
                                         Iterator<AssetItem> iterator) {
        int skipped = 0;
        Integer pageSize = pageRequest.getPageSize();
        int startRowIndex = pageRequest.getStartRowIndex();
        RepositoryFilter filter = new AssetItemFilter();
        List<QueryPageRow> rowList = new ArrayList<QueryPageRow>();

        while ( iterator.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            AssetItem assetItem = (AssetItem) iterator.next();

            // Filter surplus assets
            if ( filter.accept( assetItem,
                                RoleTypes.PACKAGE_READONLY ) ) {

                // Cannot use AssetItemIterator.skip() as it skips non-filtered
                // assets whereas startRowIndex is the index of the
                // first displayed asset (i.e. filtered)
                if ( skipped >= startRowIndex ) {
                    rowList.add( QueryPageRowCreator.makeQueryPageRow( assetItem ) );
                }
                skipped++;
            }
        }
        return rowList;
    }
}
