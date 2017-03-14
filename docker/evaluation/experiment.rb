#!/bin/ruby
require 'pty'
require 'expect'
require 'json'
require 'fileutils'

def run_osgi_analysis(analysis, target, extra='', iterations=10)
  FileUtils.rmdir 'felix-cache'
  puts "Run experiment #{analysis} with target #{target}"
  timings = []
  before = Time.now
  PTY.spawn('java -Xmx16G -jar bin/felix.jar') do |stdout, stdin, pid|
    stdout.expect 'Welcome to Apache Felix Gogo'
    iterations.times do
      stdin.puts "ra #{analysis} #{target} #{extra}"
      loop do
        _, name, all, _, _, single = stdout.expect /([\w\-]+) has run for (\d+) ms((, (\d+) ms)|(\. The result was cached\.))/
        single = 0 if not single
        timings << {name => {:Run => all.to_i, :Analysis => single.to_i}}
        break if name == analysis
      end
      stdin.puts 'ua'
      stdout.expect "Updated #{analysis}"
    end
    Process.kill('SIGTERM', pid)
  end
  after = Time.now
  duration = (after - before) * 1000
  js = {:Target => target, :Analysis => analysis, :Whole => duration, :Timings => timings}.to_json
  target = (target.gsub '.', '-').split('/')[-1]
  FileUtils::mkdir_p "/output/#{target}/#{analysis}/#{analysis}-sootkeeper", :mode => 0777
  date = Time.now.strftime('%y-%m-%d_%H:%M:%S')
  File.open("/output/#{target}/#{analysis}/#{analysis}-sootkeeper/#{date}.json", 'w', 0777) do |f|
    f.write(js)
  end
end

def run_analysis(analysis, analysis_kind, target, extra='', iterations=10)
  puts "Run experiment #{analysis} with target #{target}"
  total_before = Time.now
  timings = []
  iterations.times do
    before = Time.now
    system("java -Xmx16G -jar analyses/#{analysis}-1.0-SNAPSHOT-jar-with-dependencies.jar #{target} #{extra}")
    after = Time.now
    duration = (after - before) * 1000
    timings << {analysis => {:Run => duration}}
  end
  total_after = Time.now
  total_duration = (total_after - total_before) * 1000
  js = {:Target => target, :Analysis => analysis, :Whole => total_duration, :Timings => timings}.to_json
  date = Time.now.strftime('%y-%m-%d_%H:%M:%S')
  target = (target.gsub '.', '-').split('/')[-1]
  FileUtils::mkdir_p "/output/#{target}/#{analysis_kind}/#{analysis}", :mode => 0777
  File.open("/output/#{target}/#{analysis_kind}/#{analysis}/#{date}.json", 'w', 0777) do |f|
    f.write(js)
  end
end
4.times { puts ('=' * 80) }
reps = 5
reps.times do

  Dir.glob('/input/*.jar') do |target|
    %w(dae peaks-direct-native peaks-reflection).each do |analysis|
      run_analysis(analysis+'-main', analysis, target)
      run_osgi_analysis(analysis, target)

    end
  end

  Dir.glob('/input/*.apk') do |target|
    run_osgi_analysis('flowdroid', target, '/platforms/ --nostatic --aplength 1 --aliasflowins --nocallbacks --layoutmode none --noarraysize')
    run_analysis('soot-infoflow-android', 'flowdroid', target, '/platforms/ --nostatic --aplength 1 --aliasflowins --nocallbacks --layoutmode none --noarraysize')
  end

  4.times { puts ('=' * 80) }

end


