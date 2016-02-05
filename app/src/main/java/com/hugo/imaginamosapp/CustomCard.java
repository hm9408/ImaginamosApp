package com.hugo.imaginamosapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import Model.App;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by hm94__000 on 05-Feb-16.
 */

/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */
public class CustomCard extends Card{
    protected TextView mTitle;
    protected TextView mSecondaryTitle;
    protected TextView mPrice;
    protected int resourceIdThumbnail;

    protected App app;

    public CustomCard(Context context, App a)
    {
        this(context, R.layout.card_inner_content, a);
    }

    public CustomCard(Context context, int innerLayout, App a) {
        super(context, innerLayout);
        init(a);
    }

    private void init(App a) {

        this.app = a;

        //Add thumbnail
        CardThumbnail cardThumbnail = new CardThumbnail(mContext);
        cardThumbnail.setUrlResource(app.getUrlImMed());
        //if (resourceIdThumbnail==0)            cardThumbnail.setDrawableResource(R.mipmap.ic_launcher);

        addCardThumbnail(cardThumbnail);

    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
        mTitle = (TextView) parent.findViewById(R.id.card_inner_title);
        mSecondaryTitle = (TextView) parent.findViewById(R.id.card_inner_secondary_title);
        mPrice = (TextView) parent.findViewById(R.id.card_inner_price);

        if (mTitle != null)
            mTitle.setText(app.getName());

        if (mSecondaryTitle != null)
            mSecondaryTitle.setText(app.getArtist());

        if (mPrice != null) {
            String p = "$"+app.getPrice()+" "+app.getCurrency();
            mPrice.setText(p);
        }

    }

    public App getApp()
    {
        return this.app;
    }
}
