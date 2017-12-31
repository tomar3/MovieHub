package com.codertal.moviehub.features.moviedetail;


import android.content.Intent;


import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.codertal.moviehub.base.adapter.BaseRecyclerViewAdapter;
import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.videos.model.Video;
import com.codertal.moviehub.features.movies.favorites.FavoriteSnackbarListener;
import com.codertal.moviehub.GlideApp;
import com.codertal.moviehub.R;
import com.codertal.moviehub.features.moviedetail.adapter.ReviewListAdapter;
import com.codertal.moviehub.features.moviedetail.adapter.TrailerListAdapter;
import com.codertal.moviehub.features.movies.MoviesFragment;
import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.data.reviews.model.Review;
import com.codertal.moviehub.features.movies.receiver.NetworkChangeBroadcastReceiver;
import com.codertal.moviehub.data.movies.local.task.MovieFavoritesQueryHandler;
import com.codertal.moviehub.utilities.NetworkUtils;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;


public class MovieDetailActivity extends AppCompatActivity implements
        MovieDetailContract.View,
        BaseRecyclerViewAdapter.OnViewHolderClickListener<Video>,
        MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener,
        FavoriteSnackbarListener.OnFavoriteSnackbarClickListener,
        NetworkChangeBroadcastReceiver.OnNetworkConnectedListener{

    @BindView(R.id.tv_year)
    TextView year;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_rating)
    TextView rating;

    @BindView(R.id.tv_overview)
    TextView overview;

    @BindView(R.id.tv_title)
    TextView title;

    @BindView(R.id.fab_fav)
    FloatingActionButton fabFavorite;

    @BindView(R.id.iv_movie_thumb)
    ImageView movieThumbnail;

    @BindView(R.id.iv_movie_background)
    ImageView movieBackground;

    @BindView(R.id.ib_play_trailer)
    ImageButton playTrailerButton;

    @BindView(R.id.sv_detail)
    ScrollView mScrollView;

    @Inject
    MovieRepository mMovieRepository;

    @InjectExtra
    Movie mMovie;

    private static final String EXPANDED_POSITIONS_KEY = "expandedPositions";
    private static final String SCROLL_POSITION_KEY = "scrollPosition";

    private MovieDetailContract.Presenter mPresenter;

    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;

    private MenuItem mShareMenuItem;
    private NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        Dart.inject(this);

        //Initialize necessary components
        mNetworkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver(this);


        toolbar.setNavigationOnClickListener(v ->
                NavUtils.navigateUpFromSameTask(MovieDetailActivity.this));

        setSupportActionBar(toolbar);

        mPresenter = new MovieDetailPresenter(this, mMovie, mMovieRepository, this);
        mPresenter.loadMovieDetails();

        setUpVideosCard();
        setUpReviewsCard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main, menu);

        //Locate ShareMenuItem and display it if a trailer is available
        mShareMenuItem = menu.findItem(R.id.menu_item_share);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_item_share){
            mPresenter.handleShareClick();
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
        writeToBundle(outState, mPresenter.getState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPresenter.restoreState(readFromBundle(savedInstanceState));
    }

    @Override
    public void writeToBundle(Bundle outState, MovieDetailContract.State state) {
        outState.putIntArray(SCROLL_POSITION_KEY, state.getScrollPositions());
        outState.putIntegerArrayList(EXPANDED_POSITIONS_KEY, state.getExpandedViewPositions());
    }

    @Override
    public MovieDetailContract.State readFromBundle(@NonNull Bundle savedInstanceState) {
        return new MovieDetailState(savedInstanceState.getIntArray(SCROLL_POSITION_KEY),
                savedInstanceState.getIntegerArrayList(EXPANDED_POSITIONS_KEY));
    }

    @Override
    public void displayMovieDetails(@NonNull Movie movie) {
        //Use Glide to load the poster url
        GlideApp.with(this)
                .load(NetworkUtils.buildPosterUrl(movie.getPosterPath()))
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.error_placeholder)
                .into(movieThumbnail);

        title.setText(movie.getTitle());
        year.setText(movie.getReleaseDate().split("-")[0]);
        String ratingConcat = movie.getVoteAverage() + "/10";
        rating.setText(ratingConcat);
        overview.setText(movie.getOverview());
    }

    @Override
    public void displayVideos(@NonNull List<Video> videos) {
        mTrailerListAdapter.updateItems(videos);
    }

    @Override
    public void displayReviews(@NonNull List<Review> reviews) {
        mReviewListAdapter.updateItems(reviews);
    }

    @Override
    public void displayBackdrop(String backdropPath) {
        //Use Glide to load the backdrop url
        GlideApp.with(this)
                .load(NetworkUtils.buildBackdropUrl(backdropPath))
                .error(R.drawable.error_placeholder)
                .placeholder(R.drawable.loading_image)
                .centerCrop()
                .into(movieBackground);
    }

    @Override
    public void displayBackdropPlayButton(boolean display) {
        playTrailerButton.setImageResource(R.drawable.ic_action_play);
        playTrailerButton.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    @Override
    public void displayNetworkError(){
        playTrailerButton.setImageResource(R.drawable.ic_cloud_off);
    }

    @Override
    public void displayNoVideosMessage() {
        Toast.makeText(this, getString(R.string.no_trailers_toast), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showShareVideoUi(String videoUrl, String movieTitle) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        String videoUri = Uri.parse(videoUrl).toString();

        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_content_start)
                        + " " + movieTitle + "\n\n"
                        + videoUri + "\n\n"
                        + getString(R.string.share_content_end));
        shareIntent.setType("text/plain");

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }else {
            Toast.makeText(this, getString(R.string.no_shareable_apps), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void fillFavoriteIcon(boolean fill) {
        if(fill) {
            fabFavorite.setImageResource(R.drawable.ic_action_favorite_filled);
        }else {
            fabFavorite.setImageResource(R.drawable.ic_action_favorite_unfilled);
        }
    }

    @Override
    public void displayFavoriteSnackbar(boolean isFavorite) {
        String title;

        //Change text based on favorited status
        if(isFavorite){
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
    public void showVideoUi(@NonNull String videoKey) {
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils.buildYouTubeUrl(videoKey)));
        trailerIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        //Check if implicit intent can be resolved
        if (trailerIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(trailerIntent);
        }else {
            Toast.makeText(this, getString(R.string.no_trailer_player_app_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void displayShareMenuItem(boolean display) {
        if(mShareMenuItem != null){
            mShareMenuItem.setVisible(display);
        }
    }

    @Override
    public int[] getScrollPositions() {
        return new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()};
    }

    @Override
    public ArrayList<Integer> getExpandedViewPositions() {
        return mReviewListAdapter.getExpandedViewPositions();
    }

    @Override
    public void scrollPage(int positionX, int positionY) {
        mScrollView.post(() -> mScrollView.scrollTo(positionX, positionY));
    }

    @Override
    public void setExpandedViewPositions(@NonNull ArrayList<Integer> expandedViewPositions) {
        mReviewListAdapter.setExpandedViewPositions(expandedViewPositions);
    }

    @Override
    public void onViewHolderClick(View view, int position, Video item) {
        mPresenter.handleVideoItemClick(item);
    }

    @OnClick(R.id.fab_fav)
    public void onFavButtonClick() {
        mPresenter.handleFavoriteClick();
    }

    @OnClick(R.id.ib_play_trailer)
    public void onTrailerPlayButtonClick(){
        mPresenter.handleBackdropPlayButtonClick();
    }

    @Override
    public void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor) {
        mPresenter.handleFavoriteQueryComplete(cursor);
    }

    @Override
    public void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri) {
        mPresenter.handleMovieFavorited(uri);
    }

    @Override
    public void onMovieFavoriteDeleteComplete(int token, Object cookie, int result) {
        mPresenter.handleMovieUnfavorited(result);
    }

    @Override
    public void onFavoriteSnackbarClick(View v) {
        mPresenter.handleFavoriteSnackbarClick();
    }

    @Override
    public void onNetworkConnected() {
        mPresenter.handleNetworkConnected();
    }

    private void setUpVideosCard() {
        View trailerSectionInclude = findViewById(R.id.cv_trailer_section);
        RecyclerView trailerList = trailerSectionInclude.findViewById(R.id.rv_card_content);

        //Create new linear layout manager and set it with the recycler view
        trailerList.setLayoutManager(new LinearLayoutManager(this));
        trailerList.setHasFixedSize(true);

        View emptyTrailersInclude = trailerSectionInclude.findViewById(R.id.empty_view_include);
        TextView emptyTrailersView = emptyTrailersInclude.findViewById(R.id.tv_empty_view);
        TextView trailerSectionTitle = trailerSectionInclude.findViewById(R.id.tv_card_header);

        trailerSectionTitle.setText(getString(R.string.trailers_header));
        emptyTrailersView.setText(getString(R.string.empty_trailers));

        //Create and set the trailer list adapter
        mTrailerListAdapter = new TrailerListAdapter(this, emptyTrailersView);
        trailerList.setAdapter(mTrailerListAdapter);
        trailerList.setNestedScrollingEnabled(false);
    }

    private void setUpReviewsCard() {

        View reviewSectionInclude = findViewById(R.id.cv_review_section);

        RecyclerView reviewList = reviewSectionInclude.findViewById(R.id.rv_card_content);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        reviewList.setLayoutManager(linearLayoutManager);

        reviewList.addItemDecoration(new DividerItemDecoration(reviewList.getContext(),
                linearLayoutManager.getOrientation()));

        reviewList.setHasFixedSize(true);

        View emptyReviewsInclude = reviewSectionInclude.findViewById(R.id.empty_view_include);
        TextView emptyReviewsView = emptyReviewsInclude.findViewById(R.id.tv_empty_view);
        TextView reviewSectionTitle = reviewSectionInclude.findViewById(R.id.tv_card_header);
        reviewSectionTitle.setText(getString(R.string.reviews_header));
        emptyReviewsView.setText(getString(R.string.empty_reviews));

        mReviewListAdapter = new ReviewListAdapter(null, emptyReviewsView);
        reviewList.setAdapter(mReviewListAdapter);
        reviewList.setNestedScrollingEnabled(false);
    }
}
