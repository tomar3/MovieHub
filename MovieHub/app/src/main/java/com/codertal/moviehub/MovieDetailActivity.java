package com.codertal.moviehub;


import android.content.ContentValues;
import android.content.Intent;


import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.codertal.moviehub.adapters.ReviewListAdapter;
import com.codertal.moviehub.adapters.TrailerListAdapter;
import com.codertal.moviehub.data.movies.local.MovieContract;
import com.codertal.moviehub.features.movies.MoviesFragment;
import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.data.reviews.Review;
import com.codertal.moviehub.data.trailers.Trailer;
import com.codertal.moviehub.recievers.NetworkChangeBroadcastReceiver;
import com.codertal.moviehub.tasks.MovieFavoritesQueryHandler;
import com.codertal.moviehub.tasks.MovieDetailsQueryLoader;
import com.codertal.moviehub.utilities.NetworkUtils;
import com.codertal.moviehub.utilities.QueryParseUtils;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailActivity extends AppCompatActivity implements
        TrailerListAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<String>,
        MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener,
        FavoriteSnackbarListener.OnFavoriteSnackbarClickListener,
        NetworkChangeBroadcastReceiver.OnNetworkConnectedListener{

    @BindView(R.id.tv_year) TextView year;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_rating) TextView rating;
    @BindView(R.id.tv_overview) TextView overview;
    @BindView(R.id.tv_title) TextView title;
    @BindView(R.id.fab_fav) FloatingActionButton fabFavorite;
    @BindView(R.id.iv_movie_thumb) ImageView movieThumbnail;
    @BindView(R.id.iv_movie_background) ImageView movieBackground;
    @BindView(R.id.ib_play_trailer) ImageButton playTrailerButton;
    @BindView(R.id.sv_detail) ScrollView mScrollView;

    private static final String EXPANDED_POSITIONS_KEY = "expandedPositions";
    private static final String SCROLL_POSITION_KEY = "scrollPosition";
    public static final String MOVIE_ID_KEY = "movieId";
    private static final int MOVIE_DETAIL_LOADER = 11;
    private static final int MOVIE_FAV_QUERY = 10;
    private static final int MOVIE_FAV_INSERT = 9;
    private static final int MOVIE_FAV_DELETE = 8;

    private ArrayList<Review> mReviews;
    private ArrayList<Trailer> mTrailers;

    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;

    private Movie mMovie;
    private MovieFavoritesQueryHandler movieFavoritesQueryHandler;
    private MenuItem mShareMenuItem;
    private NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;
    private boolean mIsFavorited;
    private boolean mHitSnackbar;
    private boolean mNetworkErrorShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        //Initialize necessary components
        movieFavoritesQueryHandler = new MovieFavoritesQueryHandler(getContentResolver(), this);
        mNetworkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver(this);
        mNetworkErrorShown = false;

        mTrailers = new ArrayList<>();
        mReviews = new ArrayList<>();
        mHitSnackbar = false;

        //Retrieve the incoming intent
        Intent incomingIntent = getIntent();

        //Set up all of the views with the passed data
        if(incomingIntent != null){

            toolbar.setNavigationOnClickListener(v ->
                    NavUtils.navigateUpFromSameTask(MovieDetailActivity.this));

            //Set the views based on the passed data
            mMovie = Parcels.unwrap(incomingIntent.getParcelableExtra(MoviesFragment.MOVIE_INFO));

            //Use Glide to load the poster url
            GlideApp.with(this)
                    .load(NetworkUtils.buildPosterUrl(mMovie.getPosterPath()))
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.error_placeholder)
                    .into(movieThumbnail);

            title.setText(mMovie.getTitle());
            year.setText(mMovie.getReleaseDate().split("-")[0]);
            String ratingConcat = mMovie.getVoteAverage() + "/10";
            rating.setText(ratingConcat);
            overview.setText(mMovie.getOverview());


            View trailerSectionInclude = findViewById(R.id.cv_trailer_section);
            View reviewSectionInclude = findViewById(R.id.cv_review_section);

            RecyclerView trailerList = trailerSectionInclude.findViewById(R.id.rv_card_content);
            RecyclerView reviewList = reviewSectionInclude.findViewById(R.id.rv_card_content);

            //Create new linear layout manager and set it with the recycler view
            trailerList.setLayoutManager(new LinearLayoutManager(this));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            reviewList.setLayoutManager(linearLayoutManager);

            reviewList.addItemDecoration(new DividerItemDecoration(reviewList.getContext(),
                    linearLayoutManager.getOrientation()));

            trailerList.setHasFixedSize(true);
            reviewList.setHasFixedSize(true);


            View emptyTrailersInclude = trailerSectionInclude.findViewById(R.id.empty_view_include);
            TextView emptyTrailersView = (TextView) emptyTrailersInclude.findViewById(R.id.tv_empty_view);
            TextView trailerSectionTitle = (TextView) trailerSectionInclude.findViewById(R.id.tv_card_header);
            trailerSectionTitle.setText(getString(R.string.trailers_header));
            emptyTrailersView.setText(getString(R.string.empty_trailers));

            //Create and set the trailer list adapter
            mTrailerListAdapter = new TrailerListAdapter(mTrailers, this, emptyTrailersView);
            trailerList.setAdapter(mTrailerListAdapter);
            trailerList.setNestedScrollingEnabled(false);

            View emptyReviewsInclude = reviewSectionInclude.findViewById(R.id.empty_view_include);
            TextView emptyReviewsView = (TextView) emptyReviewsInclude.findViewById(R.id.tv_empty_view);
            TextView reviewSectionTitle = (TextView) reviewSectionInclude.findViewById(R.id.tv_card_header);
            reviewSectionTitle.setText(getString(R.string.reviews_header));
            emptyReviewsView.setText(getString(R.string.empty_reviews));

            ArrayList<Integer> expandedViewPositions = new ArrayList<>();

            //Restore expanded reviews positions and scroll position
            if(savedInstanceState!=null){

                if(savedInstanceState.containsKey(EXPANDED_POSITIONS_KEY)){
                    expandedViewPositions = savedInstanceState.getIntegerArrayList(EXPANDED_POSITIONS_KEY);
                }

                if(savedInstanceState.containsKey(SCROLL_POSITION_KEY)){
                    final int[] position = savedInstanceState.getIntArray(SCROLL_POSITION_KEY);
                    if(position != null)
                        mScrollView.post(new Runnable() {
                            public void run() {
                                mScrollView.scrollTo(position[0], position[1]);
                            }
                        });
                }
            }

            mReviewListAdapter = new ReviewListAdapter(mReviews, emptyReviewsView, expandedViewPositions);
            reviewList.setAdapter(mReviewListAdapter);
            reviewList.setNestedScrollingEnabled(false);


            makeMovieDetailQuery();
            setSupportActionBar(toolbar);

            checkIfFavorited();

        }else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main, menu);

        //Locate ShareMenuItem and display it if a trailer is available
        mShareMenuItem = menu.findItem(R.id.menu_item_share);
        displayShareMenuItem();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_item_share){

            //Share the first trailer video if it exists
            if(!mTrailers.isEmpty()){
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                String youtubeTrailerUrl = Uri.parse(NetworkUtils.buildYouTubeUrl(mTrailers.get(0).key)).toString();
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                                getString(R.string.share_content_start)
                                + " " + mMovie.getTitle() + "\n\n"
                                + youtubeTrailerUrl + "\n\n"
                                + getString(R.string.share_content_end));
                shareIntent.setType("text/plain");

                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(shareIntent);
                }else {
                    Toast.makeText(this, getString(R.string.no_shareable_apps), Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, getString(R.string.no_trailers_toast), Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(mNetworkChangeBroadcastReceiver,
                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(mNetworkChangeBroadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save scroll position and any expanded reviews
        outState.putIntArray(SCROLL_POSITION_KEY,
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
        if(!mReviewListAdapter.getExpandedViewPositions().isEmpty()){
            outState.putIntegerArrayList(EXPANDED_POSITIONS_KEY, mReviewListAdapter.getExpandedViewPositions());
        }
    }

    private void makeMovieDetailQuery(){
        Bundle movieIdBundle = new Bundle();
        movieIdBundle.putString(MOVIE_ID_KEY, mMovie.getId().toString());
        getSupportLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, movieIdBundle, MovieDetailActivity.this);
    }

    @Override
    public void onListItemClick(int position) {
        String trailerKey = mTrailers.get(position).key;
        launchTrailerIntent(trailerKey);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new MovieDetailsQueryLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if(data != null) {
            try {
                //Load the movie information from the returned JSON object
                JSONObject movieDetailJsonResult = new JSONObject(data);
                loadMovieDetails(movieDetailJsonResult);
                displayNetworkError(false);

            } catch (Throwable t) {
                Log.e("MovieDetailActivity", "Bad JSON: "  + data );
                displayNetworkError(true);
            }

        }else{
            displayNetworkError(true);
        }
    }


    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void displayNetworkError(boolean isError){

        displayShareMenuItem();

        if(isError){
            mNetworkErrorShown = true;
            playTrailerButton.setImageResource(R.drawable.ic_cloud_off);
        }else{
            mNetworkErrorShown = false;
            playTrailerButton.setImageResource(R.drawable.ic_action_play);
        }
    }

    private void displayShareMenuItem(){
        if(mShareMenuItem != null){
            if(mTrailers.isEmpty()){
                mShareMenuItem.setVisible(false);
            }else{
                mShareMenuItem.setVisible(true);
            }
        }
    }

    public void onFavButtonClick(View v) {
        //Remove or add the movie to favorites depending on its favorited status
        if(mIsFavorited){
            removeFromFavorites();
        }else {
            addToFavorites();
        }
    }

    private void loadMovieDetails(JSONObject movieDetailJSONResult){

        mTrailers.clear();
        mReviews.clear();

        //If parse of movie detail json successful, load results
        if(QueryParseUtils.parseMovieDetailQuery(movieDetailJSONResult, mTrailers, mReviews)){

            mTrailerListAdapter.updateData(mTrailers);
            mReviewListAdapter.updateData(mReviews);

            if(mTrailers.isEmpty()){
                playTrailerButton.setVisibility(View.GONE);
            }

            //Use Glide to load the backdrop url
            GlideApp.with(this)
                    .load(NetworkUtils.buildBackdropUrl(mMovie.getBackdropPath()))
                    .error(R.drawable.error_placeholder)
                    .placeholder(R.drawable.loading_image)
                    .centerCrop()
                    .into(movieBackground);
        }
    }

    private void checkIfFavorited(){
        movieFavoritesQueryHandler.startQuery(MOVIE_FAV_QUERY, null,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{mMovie.getId().toString()},
                null);
    }

    private void addToFavorites(){

        ContentValues contentValues = new ContentValues();

        //Put the movie info into the ContentValues
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, mMovie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_YEAR, mMovie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_DESCR, mMovie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, mMovie.getPosterPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, mMovie.getBackdropPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId().toString());

        //Insert the content values via a ContentResolver
        movieFavoritesQueryHandler.startInsert(MOVIE_FAV_INSERT, null,
                MovieContract.MovieEntry.CONTENT_URI, contentValues);

    }

    private void removeFromFavorites(){
        movieFavoritesQueryHandler.startDelete(MOVIE_FAV_DELETE, null,
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{mMovie.getId().toString()});
    }

    private void setFavoriteIcon(){
        if(mIsFavorited) {
            fabFavorite.setImageResource(R.drawable.ic_action_favorite_filled);
        }else {
            fabFavorite.setImageResource(R.drawable.ic_action_favorite_unfilled);
        }
    }

    private void showFavoriteSnackbar(){
        String title;

        //Change text based on favorited status
        if(mIsFavorited){
            title = getString(R.string.snackbar_favorited_title);
        }else {
            title = getString(R.string.snackbar_unfavorited_title);
        }

        Snackbar favoriteSnackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                title, Snackbar.LENGTH_LONG);
        favoriteSnackbar.setAction(R.string.snackbar_undo_label, new FavoriteSnackbarListener(this));

        favoriteSnackbar.setActionTextColor(Color.YELLOW);
        favoriteSnackbar.show();
    }

    @Override
    public void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor) {

        //Check if results show this movie is favorited
        mIsFavorited = (cursor!=null) && (cursor.getCount() >= 1);

        if(cursor!= null){
            cursor.close();
        }

        setFavoriteIcon();
    }

    @Override
    public void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri) {
        //Display favorite icon accordingly and show snackbar if not coming from an undo operation
        if(uri != null) {
            mIsFavorited = true;
            setFavoriteIcon();

            if(mHitSnackbar){
                mHitSnackbar = false;
            }else{
                showFavoriteSnackbar();
            }
        }
    }

    @Override
    public void onMovieFavoriteDeleteComplete(int token, Object cookie, int result) {
        //Display favorite icon accordingly and show snackbar if not coming from an undo operation
        if(result != 0){
            mIsFavorited = false;
            setFavoriteIcon();

            if(mHitSnackbar){
                mHitSnackbar = false;
            }else{
                showFavoriteSnackbar();
            }
        }
    }

    @Override
    public void onFavoriteSnackbarClick(View v) {
        mHitSnackbar = true;

        //Undo the favorite
        if(mIsFavorited){
            removeFromFavorites();

        }else{  //Undo the unfavorite
            addToFavorites();
        }
    }

    public void onTrailerPlayButtonClick(View v){
        //Launch first available trailer if user clicked play button on backdrop photo
        if(!mTrailers.isEmpty()){
            launchTrailerIntent(mTrailers.get(0).key);
        }
    }

    private void launchTrailerIntent(String trailerKey){
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils.buildYouTubeUrl(trailerKey)));
        trailerIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        //Check if implicit intent can be resolved
        if (trailerIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(trailerIntent);
        }else {
            Toast.makeText(this, getString(R.string.no_trailer_player_app_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNetworkConnected() {
        //Only restart query if coming from network error
        if(mNetworkErrorShown) {
            makeMovieDetailQuery();
        }
    }
}
