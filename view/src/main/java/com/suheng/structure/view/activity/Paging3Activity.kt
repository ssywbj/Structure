package com.suheng.structure.view.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suheng.structure.view.R
import com.suheng.structure.view.kt.asEntity
import com.suheng.structure.view.kt.asEntity2
import com.suheng.structure.view.paging.GithubService
import com.suheng.structure.view.paging.Paging2VM
import com.suheng.structure.view.paging.Repo
import com.suheng.structure.view.paging.RepoPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Paging3Activity : AppCompatActivity() {

    //https://developer.android.com/codelabs/android-paging?hl=zh-cn#0
    //https://blog.csdn.net/guolin_blog/article/details/114707250
    private val repoAdapter = RepoAdapter(::setClickedItem)
    private val viewModel by lazy {
        ViewModelProvider(this)[Paging2VM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paging3)

        with(findViewById<RecyclerView>(R.id.pagingList)) {
            layoutManager = LinearLayoutManager(this@Paging3Activity)
            //adapter = repoAdapter
            adapter = repoAdapter.withLoadStateFooter(FooterAdapter {
                repoAdapter.retry()
            })
        }
        repoAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    Log.d("Wbj", "LoadState.NotLoading")
                }
                is LoadState.Loading -> {
                    Log.d("Wbj", "LoadState.Loading")
                }
                is LoadState.Error -> {
                    Log.d("Wbj", "LoadState.Error")
                }
            }
        }

        job = lifecycleScope.launch(Dispatchers.IO) {
            GithubService.create().searchRepos(1, 10).items.also {
                Log.d("Wbj", "searchRepos size: ${it.size}")
                it.map(Repo::asEntity).forEach { item -> Log.v("Wbj", "asEntity: $item") }
                it.map { repo -> repo.asEntity2() }
                    .forEach { item -> Log.v("Wbj", "asEntity2: $item") }
            }.forEach {
                Log.d("Wbj", "repo: $it")
            }

            viewModel.getPagingData().collect {
                Log.d("Wbj", "collect: $it, thread name:${Thread.currentThread().name}")
                repoAdapter.submitData(it)
            }
            /*getPagingData().collect {
                Log.d("Wbj", "collect: $it, thread name:${Thread.currentThread().name}")
                repoAdapter.submitData(it)
            }*/
        }
    }

    private val PAGE_SIZE = 10

    private val gitHubService = GithubService.create()

    private fun getPagingData(): Flow<PagingData<Repo>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { RepoPagingSource(gitHubService) }
        ).flow
    }

    class RepoAdapter(val onItemClick: (Int) -> Unit) :
        PagingDataAdapter<Repo, RecyclerView.ViewHolder>(COMPARATOR) {
        companion object {
            private val COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
                override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                    oldItem == newItem
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_paging3_adt, parent, false)
            return ContentHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ContentHolder) {
                getItem(position)?.let {
                    holder.name.text = it.name
                    holder.description.text = it.description
                    holder.starCount.text = it.starCount.toString()
                }
                holder.itemView.setOnClickListener { onItemClick(position) }
            }
        }

        class ContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.name_text)
            val description: TextView = itemView.findViewById(R.id.description_text)
            val starCount: TextView = itemView.findViewById(R.id.star_count_text)
        }
    }

    class FooterAdapter(val retry: () -> Unit) : LoadStateAdapter<FooterAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
            val retryButton: Button = itemView.findViewById(R.id.retry_button)
        }

        override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_paging3_adt_footer, parent, false)
            val holder = ViewHolder(view)
            holder.retryButton.setOnClickListener {
                retry()
            }
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
            //holder.progressBar.isVisible = loadState is LoadState.Loading
            //holder.retryButton.isVisible = loadState is LoadState.Error
            holder.progressBar.visibility =
                if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            holder.retryButton.visibility =
                if (loadState is LoadState.Error) View.VISIBLE else View.GONE
        }
    }

    private var clickedIndex: Int = -1

    private fun setClickedItem(clickedIndex: Int) {
        viewModel.clickedIndex = clickedIndex
        this.clickedIndex = clickedIndex
        Log.d("Wbj", "setClickedItem clickedIndex: $clickedIndex")
    }

    private lateinit var job: Job

    override fun onStop() {
        super.onStop()
        job.takeIf { it.isActive }?.cancel()
    }

    override fun onStart() {
        super.onStart()
        //Activity重建，ViewModel中的数据会自动保存，Activity中的不会
        Log.d("Wbj", "act clickedIndex: $clickedIndex, vm clickedIndex: ${viewModel.clickedIndex}, this: $this")
    }

}