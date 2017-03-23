# Sootkeeper experiments

This image can be built with `docker build .`.

In order to actually run the experiments you have to provide 3 docker volumes,
`/input/`, `/output/` and `/platforms/`. 
Where input contains all targets in jar/apk form, ouput will contain the results after the experiments are run, and platforms contains the Android platforms that are used by target applications.

If those folders exist in the current directory the experiment can be run with:

`docker run -ti -v $(pwd)/input:/input/ -v $(pwd)/output:/output/ -v $(pwd)/platforms:/platforms/:ro ${IMAGE_ID}`

Alternatively you can use the provided shell script `./run_experiments`, which builds the image and runs the experiments.

