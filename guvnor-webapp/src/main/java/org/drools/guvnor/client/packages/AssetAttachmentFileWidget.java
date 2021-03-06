/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.packages;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.EditorWidget;
import org.drools.guvnor.client.ruleeditor.RuleViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This wraps a file uploader utility for model packages.
 * Model packages are jar files.
 */

public abstract class AssetAttachmentFileWidget extends Composite
    implements
    EditorWidget {

    private Constants         constants = GWT.create( Constants.class );

    private FormPanel         form;
    private Button            ok;
    private RuleViewer        viewer;
    protected FormStyleLayout layout;
    protected RuleAsset       asset;

    public AssetAttachmentFileWidget(final RuleAsset asset,
                                     final RuleViewer viewer) {
        this.viewer = viewer;
        this.asset = asset;
        initWidgets( asset.getUuid(),
                     asset.getName() );
        initAssetHandlers();
    }

    public AssetAttachmentFileWidget() {
    }

    protected void initWidgets(final String uuid,
                               String formName) {
        form = new FormPanel();
        form.setAction( GWT.getModuleBaseURL() + "asset" );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        FileUpload up = new FileUpload();
        up.setName( HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH );
        HorizontalPanel fields = new HorizontalPanel();
        fields.add( getHiddenField( HTMLFileManagerFields.FORM_FIELD_UUID,
                                    uuid ) );

        ok = new Button( constants.Upload() );

        fields.add( up );
        fields.add( ok );

        form.add( fields );

        layout = new FormStyleLayout( getIcon(),
                                      formName );

        if ( !this.asset.isReadonly() ) layout.addAttribute( constants.UploadNewVersion(),
                                                           form );

        Button dl = new Button( constants.Download() );
        dl.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open( GWT.getModuleBaseURL() + "asset?" + HTMLFileManagerFields.FORM_FIELD_UUID + "=" + uuid,
                             "downloading",
                             "resizable=no,scrollbars=yes,status=no" );
            }
        } );
        layout.addAttribute( constants.DownloadCurrentVersion(),
                             dl );

        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                showUploadingBusy();
                submitUpload();
            }
        } );

        initWidget( layout );
        layout.setWidth( "100%" );
        this.setStyleName( getOverallStyleName() );
    }

    /**
     * @return The path to the icon to use.
     */
    public abstract ImageResource getIcon();

    /**
     * return the overall style name to use.
     */
    public abstract String getOverallStyleName();

    void initAssetHandlers() {
        form.addSubmitCompleteHandler( new SubmitCompleteHandler() {

            public void onSubmitComplete(SubmitCompleteEvent event) {
                LoadingPopup.close();

                if ( viewer.checkedInCommand != null ) {
                    viewer.checkedInCommand.execute();
                }
                
                if ( viewer.refreshCommand!= null ) {
                    viewer.refreshCommand.execute();
                }
                
                if ( event.getResults().indexOf( "OK" ) > -1 ) {
                    viewer.showInfoMessage( constants.FileWasUploadedSuccessfully() );
                } else {
                    ErrorPopup.showMessage( constants.UnableToUploadTheFile() );
                }
            }

        } );
    }

    protected void submitUpload() {
        form.submit();
    }

    protected void showUploadingBusy() {
        LoadingPopup.showMessage( constants.Uploading() );
    }

    private TextBox getHiddenField(String name,
                                   String value) {
        TextBox t = new TextBox();
        t.setName( name );
        t.setText( value );
        t.setVisible( false );
        return t;
    }

    public void addDescription(Widget d) {
        this.layout.addRow( d );
    }

}
