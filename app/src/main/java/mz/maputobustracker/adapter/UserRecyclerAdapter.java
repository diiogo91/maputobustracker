package mz.maputobustracker.adapter;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import mz.maputobustracker.domain.Utente;


public class UserRecyclerAdapter extends FirebaseRecyclerAdapter<Utente, UserViewHolder> {

    public UserRecyclerAdapter(
            Class<Utente> modelClass,
            int modelLayout,
            Class<UserViewHolder> viewHolderClass,
            Query ref ){
        super( modelClass, modelLayout, viewHolderClass, ref );
    }
    @Override
    protected void populateViewHolder(
            UserViewHolder userViewHolder,
            Utente utente, int i) {
        userViewHolder.text1.setText( utente.getName() );
        userViewHolder.text2.setText( utente.getEmail() );
    }
}