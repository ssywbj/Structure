package com.suheng.opengl.renderer

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class BackgroundRenderer {
    private val vertexShaderCode = """
        attribute vec4 aPosition;
        attribute vec2 aTextureCoord;
        varying vec2 vTextureCoord;
        uniform mat4 uMVPMatrix;
        void main() {
            vTextureCoord = aTextureCoord;
            gl_Position = uMVPMatrix * aPosition;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec2 vTextureCoord;
        uniform sampler2D uTexture;
        void main() {
            gl_FragColor = texture2D(uTexture, vTextureCoord);
        }
    """

    private var program: Int
    private var positionHandle: Int = 0
    private var textureCoordHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    private var textureHandle: Int = 0

    private val vertexBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer

    private val squareCoords = floatArrayOf(
        -1.0f, 1.0f, 0.0f,   // top left
        -1.0f, -1.0f, 0.0f,  // bottom left
        1.0f, -1.0f, 0.0f,   // bottom right
        1.0f, 1.0f, 0.0f     // top right
    )

    private val textureCoords = floatArrayOf(
        0.0f, 0.0f,     // top left
        0.0f, 1.0f,     // bottom left
        1.0f, 1.0f,     // bottom right
        1.0f, 0.0f      // top right
    )

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices
    private val drawListBuffer: ShortBuffer

    private val mvpMatrix = FloatArray(16)

    init {
        // initialize vertex byte buffer for shape coordinates
        val bb = ByteBuffer.allocateDirect(squareCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(squareCoords)
        vertexBuffer.position(0)

        // initialize byte buffer for the texture coordinates
        val tb = ByteBuffer.allocateDirect(textureCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        textureBuffer = tb.asFloatBuffer()
        textureBuffer.put(textureCoords)
        textureBuffer.position(0)

        // prepare shaders and OpenGL program
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)
    }

    fun draw(textureId: Int) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            positionHandle, 3,
            GLES20.GL_FLOAT, false,
            12, vertexBuffer
        )

        // get handle to fragment shader's vColor member
        textureCoordHandle = GLES20.glGetAttribLocation(program, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(textureCoordHandle)
        GLES20.glVertexAttribPointer(
            textureCoordHandle, 2,
            GLES20.GL_FLOAT, false,
            8, textureBuffer
        )

        // get handle to shape's transformation matrix
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        Matrix.setIdentityM(mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // get handle to textures
        textureHandle = GLES20.glGetUniformLocation(program, "uTexture")

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(textureHandle, 0)

        // Draw the square
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        )

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

}

class RectangleRenderer {
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 aPosition;
        void main() {
            gl_Position = uMVPMatrix * aPosition;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 uColor;
        void main() {
            gl_FragColor = uColor;
        }
    """

    private var program: Int
    private var positionHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    private var colorHandle: Int = 0

    private val vertexBuffer: FloatBuffer

    // Define the rectangle's coordinates in Normalized Device Coordinates (centered at the origin)
    private val rectangleCoords = floatArrayOf(
        -0.5f,  0.5f, 0.0f,   // top left
        -0.5f, -0.5f, 0.0f,   // bottom left
        0.5f, -0.5f, 0.0f,   // bottom right
        0.5f,  0.5f, 0.0f    // top right
    )

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices
    private val drawListBuffer: ShortBuffer

    private val mvpMatrix = FloatArray(16)

    init {
        // Initialize vertex byte buffer for shape coordinates
        val bb = ByteBuffer.allocateDirect(rectangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(rectangleCoords)
        vertexBuffer.position(0)

        // Prepare shaders and OpenGL program
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)
    }

    fun draw(color: FloatArray, width: Int, height: Int, screenWidth: Int, screenHeight: Int) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")

        // Enable a handle to the rectangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Prepare the rectangle coordinate data
        GLES20.glVertexAttribPointer(
            positionHandle, 3,
            GLES20.GL_FLOAT, false,
            12, vertexBuffer
        )

        // get handle to fragment shader's uColor member
        colorHandle = GLES20.glGetUniformLocation(program, "uColor")
        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        // Create a new projection and view matrix for the rectangle
        val ratio: Float = screenWidth.toFloat() / screenHeight.toFloat()
        val rectangleWidth = 2f * (width.toFloat() / screenWidth.toFloat())
        val rectangleHeight = 2f * (height.toFloat() / screenHeight.toFloat())
        Matrix.setIdentityM(mvpMatrix, 0)
        Matrix.orthoM(mvpMatrix, 0, -ratio, ratio, -1f, 1f, -1f, 1f)
        Matrix.translateM(mvpMatrix, 0, 0f, 0f, 0f)
        Matrix.scaleM(mvpMatrix, 0, rectangleWidth, rectangleHeight, 1f)

        // get handle to shape's transformation matrix
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the rectangle
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        )

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}

