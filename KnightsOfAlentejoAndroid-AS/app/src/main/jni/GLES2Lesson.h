//
// Created by monty on 23/11/15.
//

#ifndef LESSON02_GLES2LESSON_H
#define LESSON02_GLES2LESSON_H
namespace odb {
	class GLES2Lesson {

		void fetchShaderLocations();

		void setPerspective();

		void prepareShaderProgram();

		void clearBuffers();

		void resetTransformMatrices();

		void printVerboseDriverInformation();

		void createVBOs();

		void deleteVBOs();

		void drawGeometry(const int vertexVbo, const int indexVbo, int vertexCount,
		                  const glm::mat4 &transform);

		GLuint createProgram(const char *pVertexSource, const char *pFragmentSource);

		GLuint loadShader(GLenum shaderType, const char *pSource);

		const static float cubeVertices[16 * 5];
		const static unsigned short cubeIndices[6 * 6];

		glm::mat4 cubeTransformMatrix;
		glm::mat4 projectionMatrix;

		GLint vertexAttributePosition;
		GLint modelMatrixAttributePosition;
		GLint samplerUniformPosition;
		GLint textureCoordinatesAttributePosition;
		GLint projectionMatrixAttributePosition;
		GLuint gProgram;
		GLuint uView;

		//VBO stuff
		GLuint vboCubeVertexDataIndex;
		GLuint vboCubeVertexIndicesIndex;

		GLuint vboBillboardVertexDataIndex;
		GLuint vboBillboardVertexIndicesIndex;

		GLuint vboFloorVertexDataIndex;
		GLuint vboFloorVertexIndicesIndex;


		std::vector<std::shared_ptr<NativeBitmap>> mBitmaps;
		std::vector<std::shared_ptr<Texture>> mTextures;
	public:
		GLES2Lesson();

		~GLES2Lesson();

		bool init(float w, float h, const std::string &vertexShader,
		          const std::string &fragmentShader);

		void setTexture(std::vector<std::shared_ptr<NativeBitmap>> textures);

		void render(std::array<std::array<int, 20>, 20> map, std::array<std::array<int, 20>, 20> actors );

		void shutdown();

		void tick();

		void setCameraPosition(float x, float y);

		glm::vec2 cameraPosition;
		static const float billboardVertices[20];
		static const unsigned short billboardIndices[6];
		static const float floorVertices[20];
		static const unsigned short floorIndices[6];
	};
}
#endif //LESSON02_GLES2LESSON_H
