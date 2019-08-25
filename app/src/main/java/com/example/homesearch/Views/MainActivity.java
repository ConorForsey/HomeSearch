package com.example.homesearch.Views;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.homesearch.Controller.HomeController;
import com.example.homesearch.Adapter.HomeAdapter;
import com.example.homesearch.Controller.SwipeController;
import com.example.homesearch.Model.SwipeControllerActions;
import com.example.homesearch.R;

public class MainActivity extends AppCompatActivity {
    //Layouts
    private LinearLayout mHomesLayout;
    private LinearLayout mEditLayout;
    private Boolean mEditLayoutVisible = false;

    //Listview and arraylist adapter
    private RecyclerView recyclerView;
    private HomeAdapter mHomeAdapter;

    //Initialise and declare controller
    HomeController controller;

    //Search edit text
    private EditText mSearchText;
    private Boolean mSearchVisible = false;

    //Search Button
    private Button mSearchButton;

    //Search data to pass to ViewActivity
    public static final String EXTRA_ID = "com.example.homesearch.ID";
    public static final String EXTRA_MESSAGE = "com.example.homesearch.LOCATION";
    private String mHomeID;
    private String mMessage;

    private EditText mHomeName;
    private EditText mAddress;

    private SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declare controller
        controller = new HomeController(MainActivity.this);

        //Declare layouts
        mHomesLayout = (LinearLayout)findViewById(R.id.uiHomesLayout);
        mEditLayout = (LinearLayout)findViewById(R.id.uiEditLayout);

        //Hide edit layout
        mEditLayout.setVisibility(View.GONE);

        //Recycler view
        recyclerView = (RecyclerView)findViewById(R.id.uiHomesRecycler);

        //Search EditText
        mSearchText = (EditText)findViewById(R.id.uiSearchEditText);
        mSearchText.setVisibility(View.GONE);

        //Search button
        mSearchButton = (Button)findViewById(R.id.uiSearchButton);
        mSearchButton.setVisibility(View.GONE);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Declare and populate adapter
        mHomeAdapter = new HomeAdapter(controller.getAllHomes());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mHomeAdapter);

        //Inputs for adding a home to database
        mHomeName = (EditText) findViewById(R.id.uiHomeNameEditText);
        mAddress = (EditText) findViewById(R.id.uiAddressEditText);

        //Set the Home layout to visible
        mHomesLayout.setVisibility(View.VISIBLE);

        //On click listener for the homes recycler view
        mHomeAdapter.setOnItemClickListener(new HomeAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                mMessage = mHomeAdapter.getHome(position).getmAddress();
                mHomeID = mHomeAdapter.getHome(position).getmName();
                sendIntent();
            }
        });

        //
        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                controller.removeData(mHomeAdapter.getHomeID(position));

                toastMessage("Location Removed!");
                mHomeAdapter = new HomeAdapter(controller.getAllHomes());
                recyclerView.setAdapter(mHomeAdapter);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    //Send intent
    private void sendIntent(){
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra(EXTRA_ID, mHomeID);
        intent.putExtra(EXTRA_MESSAGE, mMessage);
        startActivity(intent);
    }

    //Search button
    public void Search(View v){
        //Intent message
        mHomeID = "SEARCH";
        mMessage = mSearchText.getText().toString();
        //send intent
        sendIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //This adds the menu to the action bar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uiHomeIcon:
                clearScreen();
                break;
            case R.id.uiEditIcon:
                //Add or remove stored locations
                if(mEditLayoutVisible){
                    mEditLayoutVisible = false;
                    mEditLayout.setVisibility(View.GONE);
                    mHomesLayout.setVisibility(View.VISIBLE);
                }else{
                    mEditLayoutVisible = true;
                    mEditLayout.setVisibility(View.VISIBLE);
                    mHomesLayout.setVisibility(View.GONE);
                    mSearchText.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.GONE);
                }
                break;
            case R.id.uiSearchIcon:
                //If statement is used as a toggle switch
                if (mSearchVisible) {
                    mSearchVisible = false;
                    mSearchText.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.GONE);
                    mEditLayout.setVisibility(View.GONE);
                } else {
                    mSearchVisible = true;
                    mEditLayoutVisible = false;
                    mSearchText.setVisibility(View.VISIBLE);
                    mSearchButton.setVisibility(View.VISIBLE);
                    mHomesLayout.setVisibility(View.VISIBLE);
                    mEditLayout.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void clearScreen(){
        mSearchVisible = false;
        mSearchText.setVisibility(View.GONE);
        mSearchButton.setVisibility(View.GONE);
        mEditLayoutVisible = false;
        mEditLayout.setVisibility(View.GONE);
        mHomesLayout.setVisibility(View.VISIBLE);
    }

    public void onClickAddData(View v){
        AddData();
    }

    private void AddData(){
        if(TextUtils.isEmpty(mHomeName.getText()) || TextUtils.isEmpty(mAddress.getText())){
            toastMessage("Please Input a Name and Address!");
        }else{
            boolean insertData = controller.addData(mHomeName.getText().toString(), mAddress.getText().toString());
            if(insertData){
                toastMessage("Location Added!");
                //Clear input text fields
                mHomeName.getText().clear();
                mAddress.getText().clear();
                mHomeAdapter = new HomeAdapter(controller.getAllHomes());
                recyclerView.setAdapter(mHomeAdapter);
                clearScreen();
            } else toastMessage("Problem Adding Item to Database");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
