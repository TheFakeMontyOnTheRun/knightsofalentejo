//
// Created by monty on 23/11/15.
//

#ifndef LESSON02_GLES2LESSON_H
#define LESSON02_GLES2LESSON_H

namespace odb {

	enum EFadeState {
		kNormal,
		kFadingIn,
		kFadingOut
	};

	enum EGeometryType {
		kFloor,
		kWalls,
		kBillboard,
		kSkyBox
	};

	enum ECameraMode {
		kGlobalCamera,
		kChaseOverview,
		kFirstPerson,
		kTotal
	};

	enum ETextures {
		None,
		Grass,
		Bricks,
		Arch,
		Bars,
		Begin,
		Exit,
		BricksBlood,
		BricksCandles,
		Boss0,
		Boss1,
		Boss2,
		Cuco0,
		Cuco1,
		Cuco2,
		Demon0,
		Demon1,
		Demon2,
		Lady0,
		Lady1,
		Lady2,
		Bull0,
		Bull1,
		Bull2,
		Falcon0,
		Falcon1,
		Falcon2,
		Turtle0,
		Turtle1,
		Turtle2,
		Shadow,
		CursorGood0,
		CursorGood1,
		CursorGood2,
		CursorBad0,
		CursorBad1,
		CursorBad2,
		Ceiling,
		CeilingDoor,
		CeilingBegin,
		CeilingEnd,
		Splat0,
		Splat1,
		Splat2,
		CeilingBars,
		Skybox,
	};

	using IntGameMap = std::array<std::array<ETextures , 20>, 20>;
	using IntField = std::array<std::array<int, 20>, 20>;
	using LightMap = IntField;
	using Shade = float;
	using AnimationList = std::map< int, std::tuple<glm::vec2, glm::vec2, long> >;
	using CRenderingBatchElement = std::tuple< glm::vec3, EGeometryType, Shade  >;

	static const long kAnimationLength = 750;

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
		const static unsigned short cubeIndices[6 * 4];

		const static int kSkyTextureLength = 400;

		glm::mat4 cubeTransformMatrix;
		glm::mat4 projectionMatrix;

		GLint vertexAttributePosition;
		GLint modelMatrixAttributePosition;
		GLint samplerUniformPosition;
		GLint textureCoordinatesAttributePosition;
		GLint projectionMatrixAttributePosition;
		GLuint gProgram;
		GLuint uView;
		GLuint uMod;
		//VBO stuff
		GLuint vboCubeVertexDataIndex;
		GLuint vboCubeVertexIndicesIndex;

		GLuint vboBillboardVertexDataIndex;
		GLuint vboBillboardVertexIndicesIndex;

		GLuint vboFloorVertexDataIndex;
		GLuint vboFloorVertexIndicesIndex;

		GLuint vboSkyVertexDataIndex;
		GLuint vboSkyVertexIndicesIndex;


		GLint fadeUniform;


		std::vector<std::shared_ptr<NativeBitmap>> mBitmaps;
		std::vector<std::shared_ptr<Texture>> mTextures;

		glm::vec2 mCameraTarget;
		glm::vec3 mCurrentCharacterPosition;
		int mRotationTarget = 0;
		bool mLongPressing = false;
		int mCameraRotation = 0;
		long mFloorNumber = 0;
		glm::vec3 mClearColour;
		glm::vec4 mFadeColour = glm::vec4( 0.0f, 0.0f, 0.0f, 1.0f );
		EFadeState mFadeState = EFadeState::kNormal;
		glm::mat4 getSkyTransform( long offset );
		glm::mat4 getFloorTransform(glm::vec3 translation);
		glm::mat4 getBillboardTransform(glm::vec3 translation);
		glm::mat4 getCubeTransform(glm::vec3 translation);
		void consumeRenderingBatches(long animationTime);
		void produceRenderingBatches(IntGameMap map, IntGameMap actors, IntGameMap splats, LightMap lightmap, IntField ids, AnimationList movingCharacters, long animationTime);
		std::map< ETextures, std::vector< CRenderingBatchElement>> batches;
		ECameraMode mCameraMode = ECameraMode::kFirstPerson;
	public:
		GLES2Lesson();

		~GLES2Lesson();

		bool init(float w, float h, const std::string &vertexShader,
		          const std::string &fragmentShader);

		void setTexture(std::vector<std::shared_ptr<NativeBitmap>> textures);

		void render(IntGameMap map, IntGameMap actors, IntGameMap splats, LightMap lightmap, IntField ids, AnimationList movingCharacters, long animationTime);

		void shutdown();

		void toggleCloseUpCamera();

		void setCameraPosition(float x, float y);

		void setCursorAt( float x, float y );

		void setClearColour( float r, float g, float b );

		void startFadingIn();

		void startFadingOut();

		void updateCamera( long ms );

		glm::vec2 cameraPosition;
		glm::vec2 cursorPosition;
		static const float billboardVertices[20];
		static const unsigned short billboardIndices[6];
		static const float floorVertices[20];
		static const unsigned short floorIndices[6];

		static const float skyVertices[20];
		static const unsigned short skyIndices[6];

		void invalidateCachedBatches();
		void rotateLeft();
		void rotateRight();
		bool isAnimating();
		void updateFadeState(long ms);
		void setFloorNumber( long floor );

		void onReleasedLongPressingMove();
		void onLongPressingMove();
		bool isLongPressing();
	};
}
#endif //LESSON02_GLES2LESSON_H
