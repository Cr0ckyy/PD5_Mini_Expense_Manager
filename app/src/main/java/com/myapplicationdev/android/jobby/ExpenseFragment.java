package com.myapplicationdev.android.jobby;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapplicationdev.android.jobby.Model.Data;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {


    //Todo: Firebase database..
    FirebaseAuth myFirebaseAuth;
    DatabaseReference myExpenseDatabaseReference;

    //Todo: Recyclerview..
    RecyclerView recyclerView;
    TextView myTextViewExpenseSumResult;

    //Todo: Edt data item;
    EditText myEditTextAmount, myEditTextType, myEditTextNote;

    //Todo: Buttons
    Button btnUpdate, btnDelete;

    //Todo: Data variable
    String type;
    String note;
    int amount;
    String myFirebaseKey;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Todo:  Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_expense, container, false);

        myFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = myFirebaseAuth.getCurrentUser();
        assert mUser != null;
        String uid = mUser.getUid();

        myExpenseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        myTextViewExpenseSumResult = myView.findViewById(R.id.expense_txt_result);

        recyclerView = myView.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        myExpenseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                int myExpenseSum = 0;

                for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {

                    Data data = myDataSnapshot.getValue(Data.class);
                    myExpenseSum += data.getAmount();
                    String stringExpenseSum = String.valueOf(myExpenseSum);

                    myTextViewExpenseSumResult.setText("$" + stringExpenseSum + ".00");

                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Todo:  Init Firebase Recycler options
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(myExpenseDatabaseReference, Data.class)
                        .setLifecycleOwner(this)
                        .build();


        FirebaseRecyclerAdapter<Data, myViewHolder> adapter =
                new FirebaseRecyclerAdapter<Data, myViewHolder>
                        (options) {

                    @NotNull
                    @Override
                    public myViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_expense, parent, false);
                        return new myViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull ExpenseFragment.myViewHolder holder, int position, @NonNull @NotNull Data model) {
                        holder.setDate(model.getDate());
                        holder.setType(model.getType());
                        holder.setNote(model.getNote());
                        holder.setamount(model.getAmount());

                        holder.mView.setOnClickListener(view -> {

                            myFirebaseKey = getRef(position).getKey();
                            type = model.getType();
                            note = model.getNote();
                            amount = model.getAmount();

                            updateDataItem();
                        });
                    }

                };
        recyclerView.setAdapter(adapter);
    }

    static class myViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public myViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        void setamount(int amount) {
            TextView myAmount = mView.findViewById(R.id.amount_txt_expense);

            String stringAmount = String.valueOf(amount);

            myAmount.setText(stringAmount);

        }

    }


    void updateDataItem() {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.activity_update, null);
        myAlertDialog.setView(myView);

        myEditTextAmount = myView.findViewById(R.id.amount_edt);
        myEditTextNote = myView.findViewById(R.id.note_edt);
        myEditTextType = myView.findViewById(R.id.type_edt);


        myEditTextType.setText(type);
        myEditTextType.setSelection(type.length());

        myEditTextNote.setText(note);
        myEditTextNote.setSelection(note.length());

        myEditTextAmount.setText(String.valueOf(amount));
        myEditTextAmount.setSelection(String.valueOf(amount).length());


        btnUpdate = myView.findViewById(R.id.btn_upd_Update);
        btnDelete = myView.findViewById(R.id.btnuPD_Delete);

        final AlertDialog dialog = myAlertDialog.create();

        // TODO : Update method
        btnUpdate.setOnClickListener(view -> {

            type = myEditTextType.getText().toString().trim();
            note = myEditTextNote.getText().toString().trim();


            String stringAmount = myEditTextAmount.getText().toString().trim();
            int intAmount = Integer.parseInt(stringAmount);
            String mDate = DateFormat.getDateInstance().format(new Date());

            Data data = new Data(intAmount, type, note, myFirebaseKey, mDate);
            myExpenseDatabaseReference.child(myFirebaseKey).setValue(data);

            dialog.dismiss();
        });

        // TODO :  Delete method
        btnDelete.setOnClickListener(view -> {

            myExpenseDatabaseReference.child(myFirebaseKey).removeValue();

            dialog.dismiss();
        });


        dialog.show();


    }


}
