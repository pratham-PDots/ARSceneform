package com.example.arsceneform

import android.graphics.ColorSpace.Model
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.arsceneform.databinding.ActivityMainBinding
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Material
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlin.math.abs
import com.google.ar.sceneform.rendering.Color as ARColor


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var arFragment: ArFragment? = null
    private var rectangleModel: ModelRenderable? = null
    private var rectangleNode: TransformableNode? = null

    private var arImages: MutableList<ARImage> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        binding.mybutton.setOnClickListener {
            appearAtPoint(
                arFragment!!.requireView().width / 2.0f,
                arFragment!!.requireView().height / 2.0f
            )
        }
    }

    private fun makeModel(width: Float, height: Float) {
        val color = ARColor(0.3f, 0.5f, 0.8f, 0.5f)
        MaterialFactory.makeTransparentWithColor(this, color)
            .thenAccept { material: Material? ->
                ShapeFactory.makeCube(
                    Vector3(width, .01f, height),
                    Vector3(0.0f, 0.05f, 0.0f),
                    material
                ).let {
                    rectangleModel = it
                    setNodeRenderable(it)
                }
            }
    }

    private fun setNodeRenderable(model: ModelRenderable) {
        rectangleNode?.renderable = model
        rectangleNode?.select()
        val rotationQuaternion = Quaternion.eulerAngles(Vector3(90f, 0f, 0f))
        rectangleNode?.worldRotation = rotationQuaternion
    }

    private fun appearAtPoint(x: Float, y: Float) {
        val hitResult = getHitResult(x, y)
        val anchor = hitResult.createAnchor()
        val anchorNode = AnchorNode(anchor)

        val dimensions: FloatArray = getWidthHeight(anchorNode)

        anchorNode.setParent(arFragment!!.arSceneView.scene)
        rectangleNode = TransformableNode(arFragment!!.transformationSystem)
        rectangleNode?.setParent(anchorNode)

        makeModel(dimensions[0], dimensions[1])
    }

    private fun getWidthHeight(centerAnchorNode: AnchorNode): FloatArray {
        val result = floatArrayOf(.5f, .7f)
        val diff = arFragment!!.requireView().width.toFloat() / 20.0f
        val hitResult = getHitResult(arFragment!!.requireView().width.toFloat() / 2.0f + diff,
            arFragment!!.requireView().height / 2.0f)
        val anchor = hitResult.createAnchor()
        val anchorNode = AnchorNode(anchor)

        val centerAnchorCoordinates = centerAnchorNode.worldPosition
        val rightAnchorCoordinates = anchorNode.worldPosition
        result[0] = abs((rightAnchorCoordinates.x - centerAnchorCoordinates.x) * 20)
        val ratio = (arFragment!!.requireView().height / arFragment!!.requireView().width).toFloat()
        result[1] =  ratio * result[0]

        arImages.add(ARImage(
            width = result[0],
            height = result[1],
            x = centerAnchorCoordinates.x,
            y = centerAnchorCoordinates.y
        ))
        Log.d("PKJ", arImages.toString())
        return result
    }

    private fun getHitResult(x: Float, y: Float) :HitResult {
        val hitResult: List<HitResult> = arFragment!!.arSceneView.arFrame!!.hitTest(x, y)
        return hitResult[0]
    }


}