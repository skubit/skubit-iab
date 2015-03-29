/**
 * Copyright 2015 Skubit
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
package com.skubit.iab.activities;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skubit.currencies.Bitcoin;
import com.skubit.currencies.Satoshi;
import com.skubit.iab.R;
import com.skubit.iab.Utils;
import com.skubit.shared.dto.TransactionDto;
import com.skubit.shared.dto.TransactionState;
import com.skubit.shared.dto.TransactionType;

import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionDetailsActivity extends ActionBarActivity {

    private DecimalFormat mFormat;

    public static Intent newIntent(String data, String packageName) {
        Intent intent = new Intent();
        intent.setClassName(packageName, TransactionDetailsActivity.class.getName());
        intent.putExtra("transaction", data);
        return intent;
    }

    private static final String formatCurrencyAmount(BigDecimal balanceNumber) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(8);
        numberFormat.setMinimumFractionDigits(4);

        if (balanceNumber.compareTo(BigDecimal.ZERO) == -1) {
            balanceNumber = balanceNumber.multiply(new BigDecimal(-1));
        }

        return numberFormat.format(balanceNumber);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_transactiondetails);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        //  toolbar.setNavigationIcon(R.drawable.ic_action_share);
        mFormat = new DecimalFormat("0.00000000");

        //TextView from = (TextView) this.findViewById(R.id.transactiondetails_from);
        TextView to = (TextView) this.findViewById(R.id.transactiondetails_to);
        TextView notes = (TextView) this.findViewById(R.id.transactiondetails_notes);
        TextView amount = (TextView) this.findViewById(R.id.transactiondetails_amount);
        TextView status = (TextView) this.findViewById(R.id.transactiondetails_status);
        TextView date = (TextView) this.findViewById(R.id.transactiondetails_date);
        TextView label = (TextView) this.findViewById(R.id.transactiondetails_label_amount);
        TextView fee = (TextView) this.findViewById(R.id.transactiondetails_fee);

        ObjectMapper mapper = new ObjectMapper();
        TransactionDto transactionDto = null;
        try {
            transactionDto = mapper
                    .readValue(getIntent().getStringExtra("transaction"), TransactionDto.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (transactionDto == null) {
            return;
        }

        TransactionType type = transactionDto.getTransactionType();
        label.setText(Utils.transactionToText(type));

        if (TransactionType.SEND.equals(type)) {
            to.setText(transactionDto.getAddress());
        }

        formatStatus(status, transactionDto.getTransactionState());
        formatBalance(amount, transactionDto.getAmount(), type);
        fee.setText(new Bitcoin(new Satoshi(transactionDto.getFee())).getDisplay());

        if (!"\"\"".equals(transactionDto.getNote())) {
            notes.setText(transactionDto.getNote());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, 'at' hh:mma zzz");
        try {
            date.setText(dateFormat.format(transactionDto.getCreatedDate()));
        } catch (ParseException e) {
            e.printStackTrace();
            date.setText(null);
        }

    }

    private void formatStatus(TextView view, TransactionState status) {
        view.setText(status.name());
    }

    private void formatBalance(TextView view, String amount, TransactionType transactionType) {
        String balanceString = formatCurrencyAmount(new BigDecimal(amount));

        boolean isPurchaseOrSend = TransactionType.PURCHASE.equals(transactionType)
                || TransactionType.SEND.equals(transactionType);
        int color = isPurchaseOrSend ? R.color.transaction_negative : R.color.transaction_positive;

        view.setText(balanceString + " BTC");
        view.setTextColor(getResources().getColor(color));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.none, R.anim.push_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.push_out_right);
    }

}

