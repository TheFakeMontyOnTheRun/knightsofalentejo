#include <stdint.h>
#include <jni.h>
#include <android/bitmap.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <string>
#include <vector>
#include <tuple>
#include <utility>
#include <array>
#include <memory>
#include <stdio.h>
#include <map>
#include <stdlib.h>
#include <math.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

#include "NativeBitmap.h"
#include "Texture.h"
#include "GLES2Renderer.h"
#include "NdkGlue.h"
#include "LightningStrategy.h"
#include "android_asset_operations.h"


std::string gVertexShader;
std::string gFragmentShader;
std::shared_ptr<odb::GLES2Renderer> gles2Lesson = nullptr;
std::vector<std::shared_ptr<odb::NativeBitmap>> textures;
std::map<int, glm::vec2> mPositions;

odb::IntGameMap map;
odb::IntGameMap snapshot;
odb::IntGameMap splat;
odb::IntField ids;
odb::LightMap lightMap;
odb::AnimationList animationList;
long animationTime = 0;
bool hasCache = false;
odb::LightMap lightMapCache;

void loadShaders(JNIEnv *env, jobject &obj) {
    AAssetManager *asset_manager = AAssetManager_fromJava(env, obj);
    FILE *fd;
    fd = android_fopen("vertex.glsl", "r", asset_manager);
    gVertexShader = readToString(fd);
    fclose(fd);
    fd = android_fopen("fragment.glsl", "r", asset_manager);
    gFragmentShader = readToString(fd);
    fclose(fd);
}

bool setupGraphics(int w, int h) {
    gles2Lesson = std::make_shared<odb::GLES2Renderer>();
    gles2Lesson->setTexture(textures);
    animationTime = 0;
    return gles2Lesson->init(w, h, gVertexShader.c_str(), gFragmentShader.c_str());
}

void renderFrame(long delta) {
    if (gles2Lesson != nullptr && textures.size() > 0) {
        gles2Lesson->updateFadeState(delta);
        gles2Lesson->render(map, snapshot, splat, lightMap, ids, animationList, animationTime);
        gles2Lesson->updateCamera(delta);
    }
}

void shutdown() {
    gles2Lesson->shutdown();
    animationList.clear();
    mPositions.clear();
    animationTime = 0;
    textures.clear();
    hasCache = false;

    for (int y = 0; y < 20; ++y) {
        for (int x = 0; x < 20; ++x) {
            lightMapCache[y][x] = 0;
        }
    }

    gles2Lesson = nullptr;
}

extern "C" {
JNIEXPORT void JNICALL Java_br_odb_GL2JNILib_onCreate(JNIEnv *env, jclass reserved,
                                                      jobject assetManager);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setCurrentCursorPosition(JNIEnv *env, jclass type, jfloat x, jfloat y);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setCameraPosition(JNIEnv *env, jclass type, jfloat x, jfloat y);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setTextures(JNIEnv *env, jclass type, jobjectArray bitmaps);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setClearColour(JNIEnv *env, jclass type, jfloat r, jfloat g, jfloat b);


JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_rotateLeft(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_rotateRight(JNIEnv *env, jclass type);

JNIEXPORT jboolean JNICALL
Java_br_odb_GL2JNILib_isAnimating(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_toggleCloseupCamera(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL Java_br_odb_GL2JNILib_onDestroy(JNIEnv *env, jclass obj);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setActorIdPositions(JNIEnv *env, jclass type, jintArray ids_);


JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_onReleasedLongPressingMove(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_onLongPressingMove(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_fadeOut(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_fadeIn(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL Java_br_odb_GL2JNILib_init(JNIEnv *env, jclass obj,
                                                  jint width, jint height);
JNIEXPORT void JNICALL Java_br_odb_GL2JNILib_step(JNIEnv *env, jclass type, jlong delta);

JNIEXPORT void JNICALL Java_br_odb_GL2JNILib_setFloorNumber(JNIEnv *env, jclass type, jlong floor);

JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setMapWithSplatsAndActors(JNIEnv *env, jclass type, jintArray map_,
                                                jintArray actors_, jintArray splats_);

};

extern "C" JNIEXPORT void JNICALL Java_br_odb_GL2JNILib_onCreate(JNIEnv *env, jclass reserved,
                                                                 jobject assetManager) {
    loadShaders(env, assetManager);
}

extern "C" JNIEXPORT void JNICALL Java_br_odb_GL2JNILib_init(JNIEnv *env, jclass obj,
                                                             jint width, jint height) {
    setupGraphics(width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_step(JNIEnv *env, jclass type, jlong delta) {
    renderFrame(delta);

    auto it = animationList.begin();
    while (it != animationList.end()) {
        if (animationTime - (std::get<2>(it->second)) >= odb::kAnimationLength) {
            it = animationList.erase(it);
        } else {
            it = std::next(it);
        }
    }

    animationTime += delta;
}

extern "C" JNIEXPORT void JNICALL Java_br_odb_GL2JNILib_onDestroy(JNIEnv *env, jclass obj) {
    shutdown();
}

std::shared_ptr<odb::NativeBitmap> makeNativeBitmapFromJObject(JNIEnv *env, jobject bitmap) {

    void *addr;
    AndroidBitmapInfo info;
    int errorCode;

    if ((errorCode = AndroidBitmap_lockPixels(env, bitmap, &addr)) != 0) {
        LOGI("error %d", errorCode);
    }

    if ((errorCode = AndroidBitmap_getInfo(env, bitmap, &info)) != 0) {
        LOGI("error %d", errorCode);
    }

    LOGI("bitmap info: %d wide, %d tall, %d ints per pixel", info.width, info.height, info.format);


    long size = info.width * info.height * info.format;
    int *pixels = new int[size];
    memcpy(pixels, addr, size * sizeof(int));
    auto toReturn = std::make_shared<odb::NativeBitmap>(info.width, info.height, pixels);

    if ((errorCode = AndroidBitmap_unlockPixels(env, bitmap)) != 0) {
        LOGI("error %d", errorCode);
    }

    return toReturn;
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setTextures(JNIEnv *env, jclass type, jobjectArray bitmaps) {
    int length = env->GetArrayLength(bitmaps);
    textures.clear();
    for (int c = 0; c < length; ++c) {
        textures.push_back(
                makeNativeBitmapFromJObject(env, env->GetObjectArrayElement(bitmaps, c)));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setCameraPosition(JNIEnv *env, jclass type, jfloat x, jfloat y) {
    if (gles2Lesson != nullptr) {
        gles2Lesson->setCameraPosition(x, y);
    }
}


extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setMapWithSplatsAndActors(JNIEnv *env, jclass type, jintArray map_,
                                                jintArray actors_, jintArray splats_) {
    jint *level = env->GetIntArrayElements(map_, NULL);
    jint *actors = env->GetIntArrayElements(actors_, NULL);
    jint *splats = env->GetIntArrayElements(splats_, NULL);

    int position;
    for (int y = 0; y < 20; ++y) {
        for (int x = 0; x < 20; ++x) {
            position = (y * 20) + x;
            map[y][x] = (odb::ETextures) level[position];
            snapshot[y][x] = (odb::ETextures) actors[position];
            splat[y][x] = (odb::ETextures) splats[position];
            lightMap[y][x] = lightMapCache[y][x];
        }
    }

    for (int y = 0; y < 20; ++y) {
        for (int x = 0; x < 20; ++x) {

            if (map[y][x] == odb::ETextures::BricksCandles) {

                if (!hasCache) {
                    odb::LightningStrategy::castLightInAllDirections(lightMapCache, 255, map, x, y);
                    odb::LightningStrategy::castLightInAllDirections(lightMap, 255, map, x, y);
                }
            }

            //splat?
            if (snapshot[y][x] != odb::ETextures::None) {
//				odb::LightningStrategy::castPointLight( lightMap, 16, map, x, y);
            }

        }
    }

    hasCache = true;

    env->ReleaseIntArrayElements(map_, level, 0);
    env->ReleaseIntArrayElements(actors_, actors, 0);
    env->ReleaseIntArrayElements(splats_, splats, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setCurrentCursorPosition(JNIEnv *env, jclass type, jfloat x, jfloat y) {

    if (gles2Lesson != nullptr) {
        gles2Lesson->setCursorAt(x, y);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_toggleCloseupCamera(JNIEnv *env, jclass type) {

    if (gles2Lesson != nullptr) {
        gles2Lesson->toggleCloseUpCamera();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setClearColour(JNIEnv *env, jclass type, jfloat r, jfloat g, jfloat b) {
    if (gles2Lesson != nullptr) {
        gles2Lesson->setClearColour(r, g, b);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_fadeIn(JNIEnv *env, jclass type) {

    if (gles2Lesson != nullptr) {
        gles2Lesson->startFadingIn();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_fadeOut(JNIEnv *env, jclass type) {

    if (gles2Lesson != nullptr) {
        gles2Lesson->startFadingOut();
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_br_odb_GL2JNILib_isAnimating(JNIEnv *env, jclass type) {

    if (gles2Lesson != nullptr) {
        return gles2Lesson->isAnimating();
    }
    return false;
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_rotateLeft(JNIEnv *env, jclass type) {

    if (gles2Lesson != nullptr) {
        gles2Lesson->rotateLeft();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_rotateRight(JNIEnv *env, jclass type) {

    if (gles2Lesson != nullptr) {
        gles2Lesson->rotateRight();
    }
}


void addCharacterMovement(int id, glm::vec2 previousPosition, glm::vec2 newPosition) {
    auto movement = std::make_tuple<>(previousPosition, newPosition, animationTime);

    if (animationList.count(id) > 0) {

        auto animation = animationList[id];
        auto prevPosition = std::get<0>(animation);
        animation = std::make_tuple<>(prevPosition, newPosition, animationTime);
    }

    animationList[id] = movement;
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setActorIdPositions(JNIEnv *env, jclass type, jintArray ids_) {
    jint *idsLocal = env->GetIntArrayElements(ids_, NULL);

    if (gles2Lesson == nullptr) {
        return;
    }

    int position;
    for (int y = 0; y < 20; ++y) {
        for (int x = 0; x < 20; ++x) {
            position = (y * 20) + x;
            int id = idsLocal[position];
            ids[y][x] = id;
            if (id != 0) {
                auto previousPosition = mPositions[id];

                if (previousPosition != glm::vec2(x, y)) {
                    mPositions[id] = glm::vec2(x, y);
                    addCharacterMovement(id, previousPosition, mPositions[id]);
                }
            }
        }
    }

    env->ReleaseIntArrayElements(ids_, idsLocal, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_setFloorNumber(JNIEnv *env, jclass type, jlong floor) {
    if (gles2Lesson != nullptr) {
        gles2Lesson->setFloorNumber(floor);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_onReleasedLongPressingMove(JNIEnv *env, jclass type) {

    if (gles2Lesson != nullptr) {
        gles2Lesson->onReleasedLongPressingMove();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_br_odb_GL2JNILib_onLongPressingMove(JNIEnv *env, jclass type) {

    if (gles2Lesson != nullptr) {
        gles2Lesson->onLongPressingMove();
    }
}
