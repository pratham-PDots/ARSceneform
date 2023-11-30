package com.example.arsceneform

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.arsceneform.databinding.ActivityMainBinding
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Material
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.rendering.Color as ARColor


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var arFragment: ArFragment? = null
    private var myRenderable: ModelRenderable? = null
    private var ballRenderable: ModelRenderable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        makeModelOld()
        makeModel()

//        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
//            if (myRenderable == null) {
//                return@setOnTapArPlaneListener
//            }
//
//            // Create the Anchor.
//            val anchor = hitResult.createAnchor()
//            val anchorNode = AnchorNode(anchor)
//            anchorNode.setParent(arFragment!!.arSceneView.scene)
//
//            // Create the transformable andy and add it to the anchor.
//            val andy = TransformableNode(arFragment!!.transformationSystem)
//            andy.setParent(anchorNode)
//            andy.renderable = myRenderable
//            andy.select()
//        }

//        arFragment!!.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
//            if (plane.type !== Plane.Type.HORIZONTAL_UPWARD_FACING) {
//                return@setOnTapArPlaneListener
//            }
//            // Create an anchor at the hit result
//            // Create an anchor at the hit result
//            val anchor = hitResult.createAnchor()
//
//            // Create a quad and apply the semi-transparent material
//
//            // Create a quad and apply the semi-transparent material
//            MaterialFactory.makeTransparentWithColor(
//                applicationContext,
//                ARColor(0.3f, 0.5f, 0.8f, 0.5f)
//            )
//                .thenAccept { material: Material? ->
//                    val renderable = ShapeFactory.makeCube(
//                        Vector3(plane.extentX, 0.01f, plane.extentZ),
//                        Vector3(0.0f, 0.0f, 0.0f), material
//                    )
//                    addNodeToScene(arFragment!!, anchor, renderable)
//                }
//        }



        binding.mybutton.setOnClickListener {
            appearAtCenter()
        }

        binding.mybutton2.setOnClickListener {
            rotateRectangle()
        }

    }

    private fun rotateRectangle() {
        val text = binding.editText.text
        val coord : List<Float> = text.split(",").map { it.toFloat() }
        val rotationQuaternion = Quaternion.eulerAngles(Vector3(90f,0f, 0f))
        andy?.worldRotation = rotationQuaternion
    }

    private fun addNodeToScene(
        arFragment: ArFragment,
        anchor: Anchor,
        renderable: ModelRenderable
    ) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.renderable = renderable
        arFragment.arSceneView.scene.addChild(anchorNode)
    }

    private var andy : TransformableNode? = null

    private fun appearAtCenter() {
//        var anchor1 : Anchor? = null
//        var anchor2 : Anchor? = null
        val hitResult : List<HitResult> = arFragment!!.arSceneView.arFrame!!.hitTest(
            arFragment!!.requireView().width / 2.0f,
            arFragment!!.requireView().height / 2.0f
        )

        for(hR in hitResult) {
            val anchor = hR.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment!!.arSceneView.scene)



            // Create the transformable andy and add it to the anchor.
            andy = TransformableNode(arFragment!!.transformationSystem)
            andy?.setParent(anchorNode)
            andy?.renderable = ballRenderable
            andy?.select()
            val rotationQuaternion = Quaternion.eulerAngles(Vector3(90f,0f, 0f))
            andy?.worldRotation = rotationQuaternion
            break
//            anchor1 = hR.createAnchor()
//            break
        }
//
//        Log.d("PKJ", "${arFragment!!.requireView().width} ${arFragment!!.requireView().height}")
//
//        val hitResult2 : List<HitResult> = arFragment!!.arSceneView.arFrame!!.hitTest(
//            arFragment!!.requireView().width / 2.0f,
//            arFragment!!.requireView().height / 2.0f - 100.0f
//        )
//
//        for(hR in hitResult2) {
//            anchor2 = hR.createAnchor()
//            val trackable = hR.trackable
//            if(trackable is Plane) {
//                val plane : Plane = trackable
//
//            }
//            break
//        }
//
//        placeBallAtAnchor(myRenderable!!, anchor1!!)
//        placeBallAtAnchor(myRenderable!!, anchor2!!)
//        createLineBetweenAnchors(anchor1!!, anchor2!!)
    }

    private fun placeBallAtAnchor(renderable: Renderable, anchor: Anchor) {
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment!!.arSceneView.scene)

            // Create the transformable andy and add it to the anchor.
            val andy = TransformableNode(arFragment!!.transformationSystem)
            andy.setParent(anchorNode)
            andy.renderable = renderable
            andy.select()
    }

    private fun makeModelOld() {

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
            .setSource(this, R.raw.andy)
            .build()
            .thenAccept { renderable: ModelRenderable ->
                myRenderable = renderable
            }
            .exceptionally {
                val toast =
                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                null
            }
    }

    private fun makeModel() {
        val color = ARColor(0.3f, 0.5f, 0.8f, 0.5f)
        MaterialFactory.makeTransparentWithColor(this, color)
            .thenAccept { material: Material? ->
                ballRenderable =
                    ShapeFactory.makeCube(Vector3(.5f,.01f,.7f), Vector3(0.0f, 0.15f, 0.0f), material)
            }
    }


    fun createLineBetweenAnchors(
        anchor1: Anchor,
        anchor2: Anchor
    ): AnchorNode {

        val anchorNode1 = AnchorNode(anchor1)
        val anchorNode2 = AnchorNode(anchor2)

        // Calculate the local position of the second anchor relative to the first anchor
        val worldPosition1 = anchorNode1.worldPosition
        val worldPosition2 = anchorNode2.worldPosition
        val localPosition2 = Vector3.subtract(worldPosition2, worldPosition1)

        // Create a renderable for the line
        val color = ARColor(0.3f, 0.5f, 0.8f, 0.5f)
        MaterialFactory.makeTransparentWithColor(this, color)
            .thenAccept { material: Material? ->
                val lineRenderable = ShapeFactory.makeCube(Vector3(localPosition2.length(), 0.005f, 0.005f),Vector3(0f, 0f, 0f), material)
                val linePosition = Vector3.add(localPosition2, Vector3(localPosition2.length() / 2f, 0f, 0f))
                val lineNode = AnchorNode()
                lineNode.setParent(anchorNode1)
                lineNode.localPosition = linePosition
                lineNode.renderable = lineRenderable
            }

        // Set the local position of the line to be at the midpoint between the two anchors

        // Return the parent anchor node
        return anchorNode1
    }

}