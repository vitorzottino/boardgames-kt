package com.example.boardgames

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.boardgames.databinding.ActivityMainBinding
import com.example.boardgames.databinding.DialogEditBoardGameBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            (application as
                    BoardGameApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setUpLogo()
        setUpLogo()
        setUpListeners()
        setUpRecyclerView()
    }

    private fun setUpListeners() {
        binding.buttonAddGame.setOnClickListener {
            val gameName = binding.editTextGameName.text.toString()
            val gameDescription =
                binding.editTextGameDescription.text.toString()
            val gameUrl = binding.editTextGameUrl.text.toString()

            if (gameName.isNotBlank() && gameDescription.isNotBlank()) {
                mainViewModel.insert(
                    BoardGame(
                        name = gameName,
                        description = gameDescription,
                        url = gameUrl
                    )
                )
                binding.editTextGameName.text.clear()
                binding.editTextGameDescription.text.clear()
                binding.editTextGameUrl.text.clear()
                binding.editTextGameUrl.requestFocus()
            }
        }
    }

    private fun setUpRecyclerView () {
        val adapter = MainListAdapter(
            onEditClick = { game -> showEditDialog(game) },
            onDeleteClick = { game -> mainViewModel .delete(game) }
        )
        binding.recyclerViewGames .adapter = adapter
        binding.recyclerViewGames .layoutManager = LinearLayoutManager( this)
        // Adicionar Divider
        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerViewGames .context,
            (binding.recyclerViewGames .layoutManager as
                    LinearLayoutManager ).orientation
        )
        binding.recyclerViewGames .addItemDecoration( dividerItemDecoration )
        mainViewModel .allBoardGames .observe(this) { games ->
            games?.let { adapter.setBoardGames( it) }
        }
    }


    private fun showEditDialog(boardGame: BoardGame) {
        val dialogBinding = DialogEditBoardGameBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)
        // Preenche os campos de texto com os dados do jogo atual
        dialogBinding.editTextGameName.setText(boardGame.name)
        dialogBinding.editTextGameDescription.setText(boardGame.description)
        dialogBinding.editTextGameUrl.setText(boardGame.url)
        builder.setTitle("Editar Jogo")
        builder.setPositiveButton("Salvar") { _, _ ->
            val updatedGame = boardGame.copy(
                name = dialogBinding.editTextGameName.text.toString(),
                description = dialogBinding.editTextGameDescription.text.toString(),
                url = dialogBinding.editTextGameUrl.text.toString()
            )
            mainViewModel.update(updatedGame)
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun setUpLogo() {
        Glide
            .with(this)

            .load("https://static.vecteezy.com/system/resources/previews/006/404/900/original/board-game-logo-free-vector.jpg")
                    .into(binding.imageLogo)
    }


}
